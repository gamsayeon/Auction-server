package com.ccommit.auction_server.service.serviceImpl;

import com.ccommit.auction_server.enums.ProductStatus;
import com.ccommit.auction_server.exception.AddFailedException;
import com.ccommit.auction_server.exception.InputMismatchException;
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
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.AmqpIOException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.concurrent.TimeoutException;

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
    private static final Logger logger = LogManager.getLogger(RabbitMQServiceImpl.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final static String QUEUE_NAME_1 = "auction_server_multi_test1";
    private final static String QUEUE_NAME_2 = "auction_server_multi_test2";
    @Override
    public void enqueueMassage(Bid bid) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            //LocalDateTime을 JSON으로 변환하거나 JSON에서 해당 클래스로 역직렬화하기 위해 모듈을 등록하는 코드
            objectMapper.registerModule(new JavaTimeModule());

            // 우선순위 큐에 메시지를 보내기 위해 MessageProperties 객체를 생성하고 우선순위를 설정합니다.
            MessageProperties properties = new MessageProperties();
            LocalDateTime bidTime = bid.getBidTime();
            long epochSeconds = bidTime.toEpochSecond(ZoneOffset.UTC); // LocalDateTime을 Epoch 시간(초 단위)으로 변환합니다.
            long epochMillis = (Long.MAX_VALUE - epochSeconds) * 100000;
            int priority = (int) epochMillis; // 우선순위로 사용할 정수로 변환합니다.
            properties.setPriority(priority);
            String jsonStr = objectMapper.writeValueAsString(bid);
            logger.warn(bid.getPrice() + "시간 : " + bid.getBidTime());
            // RabbitMQ에 메시지를 보냅니다. 메시지 속성과 함께 보냅니다.
            rabbitTemplate.send(exchangeName, routingKey, new Message(jsonStr.getBytes(), properties));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public static void multiEnqueueMassageTest(Bid bid){
        ConnectionFactory factory = new ConnectionFactory();
        ObjectMapper objectMapper = new ObjectMapper();
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            // Declare the queues
            channel.queueDeclare(QUEUE_NAME_1, false, false, false, null);
            channel.queueDeclare(QUEUE_NAME_2, false, false, false, null);
            // Determine the queue with the fewest messages
            int queue1MessageCount = getMessageCount(channel, QUEUE_NAME_1);
            int queue2MessageCount = getMessageCount(channel, QUEUE_NAME_2);
            String targetQueue;
            if (queue1MessageCount <= queue2MessageCount) {
                targetQueue = QUEUE_NAME_1;
            } else {
                targetQueue = QUEUE_NAME_2;
            }
            //LocalDateTime을 JSON으로 변환하거나 JSON에서 해당 클래스로 역직렬화하기 위해 모듈을 등록하는 코드
            objectMapper.registerModule(new JavaTimeModule());

            // 우선순위 큐에 메시지를 보내기 위해 MessageProperties 객체를 생성하고 우선순위를 설정합니다.
            MessageProperties properties = new MessageProperties();
            LocalDateTime bidTime = bid.getBidTime();
            long epochSeconds = bidTime.toEpochSecond(ZoneOffset.UTC); // LocalDateTime을 Epoch 시간(초 단위)으로 변환합니다.
            long epochMillis = (Long.MAX_VALUE - epochSeconds) * 100000;
            int priority = (int) epochMillis; // 우선순위로 사용할 정수로 변환합니다.
            properties.setPriority(priority);
            String jsonStr = objectMapper.writeValueAsString(bid);
            logger.warn(bid.getPrice() + "시간 : " + bid.getBidTime());

            channel.basicPublish("", targetQueue, null, jsonStr.getBytes());
            // RabbitMQ에 메시지를 보냅니다. 메시지 속성과 함께 보냅니다.
//            rabbitTemplate.send(exchangeName, routingKey, new Message(jsonStr.getBytes(), properties));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (TimeoutException e) {
            throw new RuntimeException(e);
        }
    }

    private static int getMessageCount(Channel channel, String queueName) throws IOException {
        AMQP.Queue.DeclareOk result = channel.queueDeclarePassive(queueName);
        return result.getMessageCount();
    }

//    @Override
//    @RabbitListener(queues = "${rabbitmq.queue.name}")
//    public void dequeueMassage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException {
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            objectMapper.registerModule(new JavaTimeModule());
//            String jsonStr = new String(message.getBody());
//            Bid deserializedBid = objectMapper.readValue(jsonStr, Bid.class);
//            bidPriceValidService.validBidPrice(deserializedBid.getProductId(), deserializedBid.getPrice());
//
//            logger.warn(deserializedBid.getPrice() + " 시간 : " + deserializedBid.getBidTime());
//            Bid resultBid = bidRepository.save(deserializedBid);
//            Product product = productRepository.findByProductId(resultBid.getProductId());
//            if (resultBid == null) {
//                logger.warn("입찰이 되지 않았습니다.");
//                throw new AddFailedException("BID_ADD_FAILED", deserializedBid);
//            } else if (resultBid.getPrice() == product.getHighestPrice()) {
//                product.setProductStatus(ProductStatus.AUCTION_END);
//                productRepository.save(product);
//                tossPaymentService.createPayment(resultBid.getPrice(), product.getProductName(), resultBid.getProductId());
//            } else {
//                logger.info("정상적으로 입찰 되었습니다." + deserializedBid.getPrice());
//                UserProjection recipientEmail = userRepository.findUserProjectionById(resultBid.getBuyerId());
//                //emailService.notifyAuction(recipientEmail.getEmail(), "경매 입찰", product.getProductName() + "경매에 입찰하였습니다.");
//                recipientEmail = userRepository.findUserProjectionById(product.getSaleId());
//                //emailService.notifyAuction(recipientEmail.getEmail(), "경매 입찰", product.getProductName() + "경매에 입찰하였습니다.");
//            }
//
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        } catch (InputMismatchException e) {
//            //유효성 검사 실패로 인한 exception 발생시 큐에 메세지가 남아있어 해당 메세지 재처리안함(flase)
//            channel.basicReject(tag, false);
//            //channel.basicNack(tag, false, true);
//        }catch(Exception e){
//            e.printStackTrace();
//        }
//    }


}