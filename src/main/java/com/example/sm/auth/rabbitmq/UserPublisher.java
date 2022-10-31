package com.example.sm.auth.rabbitmq;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class UserPublisher {

    @Value("${rmq.userSend.exchangeName}")
    String userExchangeName;

    @Value("${rmq.userSend.routingKey}")
    String userRouteKey;

    @Value("${rmq.userSend.queueName}")
    String queueName;

    @Autowired
    RMQService rmqService;

    public void publishToQueue(Object id){
        rmqService.publishToQueue(id,userExchangeName,queueName,userRouteKey);
    }
}
