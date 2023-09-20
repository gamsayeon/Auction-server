package com.example.auction_server.service;

import com.example.auction_server.exception.AddException;
import com.example.auction_server.exception.InputSettingException;
import com.example.auction_server.model.Bid;
import com.example.auction_server.model.Category;
import com.example.auction_server.model.Product;
import com.example.auction_server.repository.BidRepository;
import com.example.auction_server.repository.CategoryRepository;
import com.example.auction_server.repository.ProductRepository;
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

import java.util.Optional;

/***
 * 생성자 주입을 자동으로 처리 함
 */
@RequiredArgsConstructor
@Service
public class MessageService {
    private static final Logger logger = LogManager.getLogger(MessageService.class);

    @Value("${rabbitmq.exchange.name}")
    private String exchangeName;

    @Value("${rabbitmq.routing.key}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BidRepository bidRepository;

    public void sendMessage(Bid bid) {
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
    public void receiveMessage(String jsonStr) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            objectMapper.registerModule(new JavaTimeModule());
            Bid deserializedBid = objectMapper.readValue(jsonStr, Bid.class);
            this.validAuctionPrice(deserializedBid.getProductId(), deserializedBid.getPrice());
            Bid resultBid = bidRepository.save(deserializedBid);
            if (resultBid == null) {
                logger.warn("입찰이 되지 않았습니다.");
                throw new AddException("BID_2", deserializedBid);
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    public void validAuctionPrice(Long productId, int price) {
        Product product = productRepository.findByProductId(productId);
        Optional<Category> category = categoryRepository.findByCategoryId(product.getCategoryId());
        int maxPrice = category.get().getBidMaxPrice();
        int minPrice = category.get().getBidMinPrice();

        Integer currentPrice = bidRepository.findMaxPriceByProductId(productId);
        if (currentPrice == null) {
            int startPrice = product.getStartPrice();
            if (startPrice == price) {
                return;
            } else if (startPrice > price) {
                logger.warn("입찰 가격을 잘못 입력하였습니다.", price);
                throw new InputSettingException("BID_1", price);
            } else if (price - startPrice > maxPrice || price - startPrice < minPrice) {
                logger.warn("입찰 가격을 잘못 입력하였습니다.", price);
                throw new InputSettingException("BID_1", price);
            }
        } else {
            if (currentPrice >= price) {
                logger.warn("입찰 가격을 잘못 입력하였습니다.", price);
                throw new InputSettingException("BID_1", price);
            } else if (currentPrice + maxPrice < price || currentPrice + minPrice > price) {
                logger.warn("입찰 가격을 잘못 입력하였습니다.", price);
                throw new InputSettingException("BID_1", price);
            }
        }
    }
}