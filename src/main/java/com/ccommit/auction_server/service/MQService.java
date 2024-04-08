package com.ccommit.auction_server.service;

import com.ccommit.auction_server.model.Bid;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;

import java.io.IOException;

public interface MQService {
    void enqueueMassage(Bid bid);

//    void dequeueMassage(Message message, Channel channel, @Header(AmqpHeaders.DELIVERY_TAG) long tag) throws IOException;
}