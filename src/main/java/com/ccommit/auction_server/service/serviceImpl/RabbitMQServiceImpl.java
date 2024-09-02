package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.dto.BidDTO;
import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.exception.*;
import com.ccommit.auction_server.mapper.BidMapper;
import com.ccommit.auction_server.model.Bid;
import com.ccommit.auction_server.model.Product;
import com.ccommit.auction_server.service.*;
import com.ccommit.auction_server.validation.BidPriceValidator;
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
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@RequiredArgsConstructor
public class RabbitMQServiceImpl implements MQService {
    private final EmailService emailService;
    private final RabbitTemplate rabbitTemplate;
    private final ProductService productService;
    private final BidService bidService;
    private final PaymentService tossPaymentService;
    private final LockService redisLockService;
    private final DataSource MySQLDBDataSource;
    private final BidMapper bidMapper;
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

    // Listener가 메시지 처리를 시작하면 플래그를 true로 설정하여 다른 Listener에서 동시에 메시지를 처리되지 않도록 함
    private AtomicBoolean isAuctionQueueListenerProcessing = new AtomicBoolean(false);

    @Override
    public BidDTO enqueueMassage(Long buyerId, Long productId, BidDTO bidDTO) {
        Bid bid = null;
        try {
            Product product = productService.findByProductId(productId);
            bidService.validBidPrice(productId, bidDTO.getPrice());

            if (product.getProductStatus() != ProductStatus.AUCTION_PROCEEDING) {
                logger.warn("경매가 시작되지 않았습니다.");
                throw new BidFailedNotStartException("BID_FAILED_NOT_START");
            } else {
                bid = bidMapper.convertToEntity(bidDTO, productId, buyerId);
                bid.setBidTime(LocalDateTime.now());

                if (redisLockService.isProductLocked(bid.getProductId())) {
                    logger.warn(bid.getProductId() + " ID의 상품이 잠겨있습니다.");
                    throw new ProductLockedException("PRODUCT_ID_LOCKED", bid.getProductId());
                }
                MessageProperties properties = setPriorityBidTime(bid);
                String jsonStr = convertBidToMessage(bid);

                rabbitTemplate.send(exchangeName, routingKey, new Message(jsonStr.getBytes(), properties));
            }

        } catch (AmqpException e) {
            logger.error("Failed to send message: " + e.getMessage());
        } finally {
            return bidDTO;
        }
    }
    private String convertBidToMessage(Bid bid){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        try {
            String jsonStr = objectMapper.writeValueAsString(bid);
            return jsonStr;
        } catch (JsonProcessingException e) {
            throw new ConvertedFailedException("CONVERTED_FAILED", bid);
        }
    }

    private MessageProperties setPriorityBidTime(Bid bid){
        MessageProperties properties = new MessageProperties();
        LocalDateTime bidTime = bid.getBidTime();
        long epochSeconds = bidTime.toEpochSecond(ZoneOffset.UTC);
        int priority = (int) ((Long.MAX_VALUE - epochSeconds) % 255); // 0부터 255까지 우선순위 설정
        properties.setPriority(priority);
        return properties;
    }

    @Override
    @RabbitListener(queues = "${rabbitmq.queue.name}", ackMode = "MANUAL")
    public void dequeueMassage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        try {
            isAuctionQueueListenerProcessing.set(true);
            Bid deserializedBid = this.convertMessageToBid(message);
            Product product = productService.findByProductId(deserializedBid.getProductId());

            if (redisLockService.isProductLocked(deserializedBid.getProductId())) {
                rabbitTemplate.execute(enqueueChannel -> {
                    enqueueDLQ(message);
                    channel.basicReject(tag, false);
                    return null;
                });
                return;
            }

            bidService.validBidPrice(deserializedBid.getProductId(), deserializedBid.getPrice());
            Bid resultBid = bidService.saveBid(deserializedBid);

            if (resultBid.getPrice() == product.getHighestPrice()) {
                productService.saveProduct(product);
                tossPaymentService.createPayment(resultBid.getPrice(), product.getProductName(), resultBid.getProductId());
            } else {
                logger.info("정상적으로 입찰 되었습니다." + deserializedBid.getPrice());
                emailService.notifyAuctionSuccess(resultBid, product);
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
        } catch (Exception ex) {
            rabbitTemplate.execute(enqueueChannel -> {
                enqueueDLQ(message);
                // DLQ에 메시지를 전송하였으니 auction.queue에 재처리 하지 않도록 false로 설정
                // ex)DBConnectionException 등이 해당
                channel.basicReject(tag, false);
                redisLockService.lockProduct(this.convertMessageToBid(message).getProductId());
                return null;
            });
            logger.error("Handled Critical exception: " + ex.getMessage());
        } finally {
            // Listener가 메시지 처리를 완료하면 플래그를 false로 설정
            isAuctionQueueListenerProcessing.set(false);
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

    private void enqueueDLQ(Message message) {
        try {
            Message newMessage = this.incrementRetryCount(message);
            Integer retryCount = this.getRetryCount(newMessage);
            rabbitTemplate.send(dlqExchangeName, dlqRoutingKey, newMessage);
            if (retryCount >= MAX_RETRY_COUNT)
                this.sendAlertAdminEmail(message, retryCount);
        } catch (AmqpException | IllegalStateException ex) {
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

        return new Message(message.getBody(), newProperties);
    }

    @Override
    @RabbitListener(queues = "${rabbitmq.dlq.queue.name}", ackMode = "MANUAL")
    public void dlqDequeueMessage(Message message, Channel dlqChannel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
        Integer retryCount = this.getRetryCount(message);
        Bid deserializedBid = null;
        try {
            deserializedBid = this.convertMessageToBid(message);
            if(isAuctionQueueListenerProcessing.get()){
                dlqChannel.basicReject(tag, true);
            } else if (this.checkDatabaseHealth() && retryCount < MAX_RETRY_COUNT) {
                this.retryEnqueueMessage(dlqChannel, tag, message);
                redisLockService.unlockProduct(deserializedBid.getProductId());
            } else if(retryCount >= MAX_RETRY_COUNT) {
                this.resendToDLQAtEnd(dlqChannel, tag, message);
                redisLockService.lockProduct(deserializedBid.getProductId());
            }
        } catch (AmqpException | IllegalStateException ex) {
            logger.error("Failed to send message: " + ex.getMessage());
        } catch (Exception ex) {
            dlqChannel.basicReject(tag, true);
            redisLockService.lockProduct(deserializedBid.getProductId());
            logger.error("Handled Critical exception: " + ex.getMessage());
        }
    }

    private void retryEnqueueMessage(Channel dlqChannel, long tag, Message message){
        rabbitTemplate.execute(channel -> {
            rabbitTemplate.send(exchangeName, routingKey, message);
            dlqChannel.basicAck(tag, false);
            return null;
        });
    }

    private void resendToDLQAtEnd(Channel dlqChannel, long tag, Message message){
        rabbitTemplate.execute(channel -> {
            rabbitTemplate.send(dlqExchangeName, dlqRoutingKey, message);
            dlqChannel.basicAck(tag, false);
            return null;
        });
    }

    private boolean checkDatabaseHealth() {
        try (Connection connection = MySQLDBDataSource.getConnection()) {
            return connection.isValid(1000);    // 1초(1000 ms)안에 응답이 오는지 확인
        } catch (SQLException ex) {
            return false;
        }
    }

    private void sendAlertAdminEmail(Message message, int retryCount) {
        // TODO: 개발자에게 이메일 전송 로직 추가 및 개발자가 확인하여 수동으로 처리할 수 있도록 안내
    }
}