package com.ccommit.auction_server.service;

import com.ccommit.auction_server.dto.BidDTO;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;

public interface MQService {
    BidDTO enqueueMassage(Long buyerId, Long productId,BidDTO bidDTO);

    void dequeueMassage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException;

    void dlqDequeueMessage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException;
}