package com.ccommit.auction_server.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {
    @Value("${spring.rabbitmq.host}")
    private String rabbitmqHost;

    @Value("${spring.rabbitmq.port}")
    private int rabbitmqPort;

    @Value("${spring.rabbitmq.username}")
    private String rabbitmqUsername;

    @Value("${spring.rabbitmq.password}")
    private String rabbitmqPassword;

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

    /**
     * 지정된 큐 이름으로 Queue 빈을 생성
     *
     * @return Queue 빈 객체
     */
    @Bean
    public Queue queue() {
        // 우선순위 큐를 사용하도록 설정
        return QueueBuilder.durable(queueName)
                .withArgument("x-max-priority", 255) // 우선순위 개수 설정
                .build();
    }

    @Bean
    public Queue deadLetterQueue() {
        return QueueBuilder.durable(dlqQueueName).build();
    }

    /**
     * 지정된 익스체인지 이름으로 DirectExchange 빈을 생성
     *
     * @return TopicExchange 빈 객체
     */
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(exchangeName);
    }

    @Bean
    public DirectExchange deadLetterExchange() {
        return new DirectExchange(dlqExchangeName);
    }

    /**
     * 주어진 큐와 익스체인지를 바인딩하고 라우팅 키를 사용하여 Binding 빈을 생성
     *
     * @param queue    바인딩할 Queue
     * @param exchange 바인딩할 DirectExchange
     * @return Binding 빈 객체
     */
    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(routingKey);
    }

    @Bean
    public Binding deadLetterQueueBinding(Queue deadLetterQueue, DirectExchange deadLetterExchange) {
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchange).with(dlqRoutingKey);
    }
    /**
     * RabbitMQ 연결을 위한 ConnectionFactory 빈을 생성하여 반환
     *
     * @return ConnectionFactory 객체
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory();
        connectionFactory.setHost(rabbitmqHost);
        connectionFactory.setPort(rabbitmqPort);
        connectionFactory.setUsername(rabbitmqUsername);
        connectionFactory.setPassword(rabbitmqPassword);
        // QoS 설정
        connectionFactory.setCacheMode(CachingConnectionFactory.CacheMode.CONNECTION);
        connectionFactory.setConnectionCacheSize(30);
        connectionFactory.setConnectionLimit(30);
        connectionFactory.setChannelCheckoutTimeout(10000); // 채널 체크아웃 시간 (밀리초)
        connectionFactory.setChannelCacheSize(100); // 채널 캐시 크기 (메시지 수)
        connectionFactory.setRequestedHeartBeat(30); // 하트비트 요청 주기 설정 RabbitMQ와의 연결 유지를 위한 하트비트(연결을 주기적으로 확인) 요청 주기 (초)
        return connectionFactory;
    }

    /**
     * RabbitTemplate을 생성하여 반환
     *
     * @param connectionFactory RabbitMQ와의 연결을 위한 ConnectionFactory 객체
     * @return RabbitTemplate 객체
     */
    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        // JSON 형식의 메시지를 직렬화하고 역직렬할 수 있도록 설정
        rabbitTemplate.setMessageConverter(jackson2JsonMessageConverter());
        rabbitTemplate.setChannelTransacted(true); // 트랜잭션 사용 설정
        return rabbitTemplate;
    }

    /**
     * Jackson 라이브러리를 사용하여 메시지를 JSON 형식으로 변환하는 MessageConverter 빈을 생성
     *
     * @return MessageConverter 객체
     */
    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
