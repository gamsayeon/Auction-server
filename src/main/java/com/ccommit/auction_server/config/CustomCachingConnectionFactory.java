package com.ccommit.auction_server.config;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.Connection;
import org.springframework.amqp.rabbit.connection.ConnectionListener;

public class CustomCachingConnectionFactory extends CachingConnectionFactory {
    public CustomCachingConnectionFactory() {
        super();
        // ConnectionListener 등록
        this.addConnectionListener(new CustomConnectionListener());
    }

    private class CustomConnectionListener implements ConnectionListener {

        @Override
        public void onCreate(Connection connection) {
            // Connection 생성 시 처리할 로직
        }

        @Override
        public void onClose(Connection connection) {
            // Connection 종료 시 처리할 로직
            // 여기에서 새로운 채널을 생성하고 필요한 작업을 수행합니다.
            // 예를 들어:
            // createChannel();
            // channel.basicConsume(queueName, consumer);
        }
    }
}