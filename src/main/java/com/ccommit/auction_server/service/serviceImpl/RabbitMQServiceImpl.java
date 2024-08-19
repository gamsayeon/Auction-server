package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.exception.AddFailedException;
import com.ccommit.auction_server.exception.InputMismatchException;
import com.ccommit.auction_server.exception.NotMatchingException;
import com.ccommit.auction_server.exception.ProductLockedException;
import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.projection.UserProjection;
import com.ccommit.auction_server.repository.BidRepository;
import com.ccommit.auction_server.repository.ProductRepository;
import com.ccommit.auction_server.repository.UserRepository;
import com.ccommit.auction_server.service.EmailService;
import com.ccommit.auction_server.service.MQService;
import com.ccommit.auction_server.service.PaymentService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.support.ListenerExecutionFailedException;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicBoolean;

@Profile({"dev", "test", "performance"})
@Service
@RequiredArgsConstructor
public class RabbitMQServiceImpl implements MQService {
    private final BidPriceValidServiceImpl bidPriceValidService;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;
    private final ProductRepository productRepository;
    private final BidRepository bidRepository;
    private final PaymentService tossPaymentService;
    private final RedisLockServiceImpl lockService;
    private final DataSource dataSource;

    private static final Logger logger = LogManager.getLogger(RabbitMQServiceImpl.class);

    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${rabbitmq.queue.name}")
    private String queueName;

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    @Value("${rabbitmq.dlq.queue.name}")
    private String dlqQueueName;

    @Value("${rabbitmq.dlq.exchange.name}")
    private String dlqExchangeName;

    @Value("${rabbitmq.dlq.routing.key}")
    private String dlqRoutingKey;

    private static final int MAX_RETRY_COUNT = 10;

    // Listener의 상태를 추적하는 플래그
    private AtomicBoolean isListenerProcessing = new AtomicBoolean(false);

    @Override
    public void enqueueMassage(Bid bid) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            if (lockService.isProductLocked(bid.getProductId())) {
                logger.warn(bid.getProductId() + " ID의 상품이 잠겨있습니다.");
                throw new ProductLockedException("PRODUCT_ID_LOCKED", bid.getProductId());
            }
            objectMapper.registerModule(new JavaTimeModule());
            MessageProperties properties = new MessageProperties();

            LocalDateTime bidTime = bid.getBidTime();
            long epochSeconds = bidTime.toEpochSecond(ZoneOffset.UTC);
            int priority = (int) ((Long.MAX_VALUE - epochSeconds) % 255); // 0부터 255까지 우선순위 설정
            properties.setPriority(priority);

            String jsonStr = objectMapper.writeValueAsString(bid);
            logger.warn(bid.getPrice() + "시간 : " + bid.getBidTime());
            rabbitTemplate.send(exchangeName, routingKey, new Message(jsonStr.getBytes(), properties));
        } catch (AmqpException e) {
            logger.error("Failed to send message: " + e.getMessage());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @Override
    @RabbitListener(queues = "${rabbitmq.queue.name}", ackMode = "MANUAL")
    public void dequeueMassage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            // Listener가 메시지 처리를 시작하면 플래그를 true로 설정
            isListenerProcessing.set(true);
            Bid deserializedBid = convertMessageToBid(message);
            Product product = productRepository.findByProductId(deserializedBid.getProductId());

            if (lockService.isProductLocked(deserializedBid.getProductId())) {
                rabbitTemplate.execute(enqueueChannel -> {
                    enqueueDLQ(message);
                    channel.basicReject(tag, false);
                    return null;
                });
                return;
            }

            bidPriceValidService.validBidPrice(deserializedBid.getProductId(), deserializedBid.getPrice());
            Bid resultBid = bidRepository.save(deserializedBid);

            if (resultBid == null) {
                logger.warn("입찰이 되지 않았습니다.");
                throw new AddFailedException("BID_ADD_FAILED", deserializedBid);
            } else if (resultBid.getPrice() == product.getHighestPrice()) {
                product.setProductStatus(ProductStatus.AUCTION_END);
                productRepository.save(product);
                tossPaymentService.createPayment(resultBid.getPrice(), product.getProductName(), resultBid.getProductId());
            } else {
                logger.info("정상적으로 입찰 되었습니다." + deserializedBid.getPrice());
                UserProjection recipientEmail = userRepository.findUserProjectionById(resultBid.getBuyerId());
                emailService.notifyAuction(recipientEmail.getEmail(), "경매 입찰", product.getProductName() + "경매에 입찰하였습니다.");
                recipientEmail = userRepository.findUserProjectionById(product.getSaleId());
                emailService.notifyAuction(recipientEmail.getEmail(), "경매 입찰", product.getProductName() + "경매에 입찰하였습니다.");
            }
            channel.basicReject(tag, false);
        } catch (InputMismatchException ex) {
            //유효성 검사 실패로 인한 exception 발생시 큐에 메세지가 남아있어 해당 메세지 재처리안함(flase)
            channel.basicReject(tag, false);
        } catch (ListenerExecutionFailedException ex) {
            // 재처리 가능한 예외 처리(auction.queue 재처리)
            channel.basicReject(tag, true);
        } catch (IllegalStateException ex) {
            // 채널이 닫혔을 때의 처리 로직
            logger.error("Channel is closed. Exception: " + ex.getMessage());
            rabbitTemplate.execute(retryChannel -> {
                channel.basicReject(tag, true);
                return null;
            });
            // 필요한 경우, 새로운 채널을 생성하거나 다른 조치를 취합니다.
        } catch (Exception ex) {
            rabbitTemplate.execute(enqueueChannel -> {
                enqueueDLQ(message);
                // DLQ에 메시지를 전송하였으니 auction.queue에 재처리 하지 않도록 false로 설정
                // ex)DBConnectionException 등이 해당
                channel.basicReject(tag, false);
                return null;
            });
            logger.error("Handled Critical exception: " + ex.getMessage());
        } finally {
            // Listener가 메시지 처리를 완료하면 플래그를 false로 설정
            isListenerProcessing.set(false);
        }
    }

    private Bid convertMessageToBid(Message message) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String jsonStr = new String(message.getBody());
            Bid deserializedBid = objectMapper.readValue(jsonStr, Bid.class);
            if (deserializedBid != null) { // Bid로 변환 가능한 경우에만 처리
                return deserializedBid;
            } else {
                throw new NotMatchingException("BID_NOT_MATCHING", "Invalid Bid data format");
            }
        } catch (IOException e) {
            throw new NotMatchingException("BID_NOT_MATCHING", "Failed to process bid data. Please check the format and try again.");
        }
    }

    // DLQ로 메시지 전송
    private void enqueueDLQ(Message message) {
        try {
            Message newMessage = incrementRetryCount(message);
            Integer retryCount = getRetryCount(newMessage);

            if (retryCount >= MAX_RETRY_COUNT) {
                // 최대 재시도 횟수를 초과한 경우, 메시지를 DLQ로 전송 및 관리하며 개발자에게 알림
                rabbitTemplate.send(dlqExchangeName, dlqRoutingKey, newMessage);
                lockService.lockProduct(convertMessageToBid(message).getProductId());

                // TODO : 개발자에게 해당 메시지 이메일로 알림
                sendAlertEmail(message, retryCount);
            } else {
                rabbitTemplate.send(dlqExchangeName, dlqRoutingKey, newMessage);
                lockService.lockProduct(convertMessageToBid(message).getProductId());
            }
        } catch (AmqpException | IllegalStateException ex) {
            // 메시지 전송 실패
            logger.error("Failed to send message: " + ex.getMessage());
        }
    }

    private Integer getRetryCount(Message message) {
        MessageProperties properties = message.getMessageProperties();
        return (Integer) properties.getHeaders().getOrDefault("x-retry-count", 0);
    }

    private Message incrementRetryCount(Message message) {
        MessageProperties properties = message.getMessageProperties();
        Integer retryCount = (Integer) properties.getHeaders().getOrDefault("x-retry-count", 0);
        // 재시도 횟수를 증가시키고 메시지 속성에 설정
        MessageProperties newProperties = new MessageProperties();
        newProperties.setHeaders(properties.getHeaders());
        newProperties.getHeaders().put("x-retry-count", retryCount + 1);

        // 새로운 메시지 객체 생성
        return new Message(message.getBody(), newProperties);
    }

    public boolean isListenerProcessing() {
        return isListenerProcessing.get();
    }
    @Override
    @RabbitListener(queues = "${rabbitmq.dlq.queue.name}", ackMode = "MANUAL")
    public void dlqDequeueMessage(Message message, Channel dlqChannel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        Integer retryCount = getRetryCount(message);
        Bid deserializedBid = null;
        try {
            if(isListenerProcessing()){
                dlqChannel.basicReject(tag, true);
                Thread.sleep(1000); // 1초 동안 지연
                return;
            } else if (checkDatabaseHealth() && retryCount < MAX_RETRY_COUNT) {
                deserializedBid = convertMessageToBid(message);
                rabbitTemplate.execute(channel -> {
                    rabbitTemplate.send(exchangeName, routingKey, message);
                    dlqChannel.basicAck(tag, false);
                    return null;
                });
                lockService.unlockProduct(deserializedBid.getProductId());
            } else {
                deserializedBid = convertMessageToBid(message);
                lockService.lockProduct(deserializedBid.getProductId());
                rabbitTemplate.execute(channel -> {
                    rabbitTemplate.send(exchangeName, routingKey, message);
                    dlqChannel.basicAck(tag, false);
                    return null;
                });
            }
        } catch (AmqpException | IllegalStateException ex) {
            // 메시지 전송 실패
            logger.error("Failed to send message: " + ex.getMessage());
        } catch (Exception ex) {
            dlqChannel.basicReject(tag, true);
            lockService.lockProduct(deserializedBid.getProductId());
            logger.error("Handled Critical exception: " + ex.getMessage());
        }
    }

    private boolean checkDatabaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            return connection.isValid(1000);    // 1초(1000 ms)안에 응답이 오는지 확인
        } catch (SQLException ex) {
            return false;
        }
    }

    private void sendAlertEmail(Message message, int retryCount) {
        // TODO: 개발자에게 이메일 전송 로직 추가 및 개발자가 확인하여 수동으로 처리할 수 있도록 안내
    }
}