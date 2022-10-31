package com.example.sm.auth.rabbitmq;

public interface RMQService {

    void publishToQueue(Object object, String exchange, String queueName, String routingKey);

    void publishToQueue(String message, String exchange, String queueName, String routingKey);

}
