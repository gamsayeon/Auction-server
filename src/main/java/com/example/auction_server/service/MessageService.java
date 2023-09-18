package com.example.auction_server.service;

import com.example.auction_server.exception.AddException;
import com.example.auction_server.model.Bid;
import com.example.auction_server.repository.BidRepository;
import com.example.auction_server.service.serviceImpl.BidPriceValidServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/***
 * 생성자 주입을 자동으로 처리 함
 */
@RequiredArgsConstructor
@Service
public class MessageService {
    private final BidPriceValidServiceImpl bidPriceValidService;
    private static final Logger logger = LogManager.getLogger(MessageService.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final BidRepository bidRepository;

    public void enqueueMassage(Bid bid) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JavaTimeModule());
            String jsonStr = objectMapper.writeValueAsString(bid);
            rabbitTemplate.convertAndSend(exchangeName, routingKey, jsonStr);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    @RabbitListener(queues = "${rabbitmq.queue.name}")
    public void dequeueMassage(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JavaTimeModule());
            Bid deserializedBid = objectMapper.readValue(jsonStr, Bid.class);
            bidPriceValidService.validBidPrice(deserializedBid.getProductId(), deserializedBid.getPrice());

            Bid resultBid = bidRepository.save(deserializedBid);
            if (resultBid == null) {
                logger.warn("입찰이 되지 않았습니다.");
                throw new AddException("BID_2", deserializedBid);
            } else {
                logger.info("정상적으로 입찰 되었습니다.");
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }


}