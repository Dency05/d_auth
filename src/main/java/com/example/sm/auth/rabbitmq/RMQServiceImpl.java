package com.example.sm.auth.rabbitmq;

import com.amazonaws.services.athena.model.InvalidRequestException;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.text.SimpleDateFormat;
/**
 * RMQ service implementation
 */
@Component
@Slf4j
public class RMQServiceImpl implements RMQService {

    @Value("${spring.rabbitmq.host}")
    String host;
    @Value("${spring.rabbitmq.username}")
    String username;
    @Value("${spring.rabbitmq.password}")
    String password;

    @Override
    public void publishToQueue(Object object, String exchange, String queueName, String routingKey) {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);
        objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'"));
        try {
            String json = objectMapper.writeValueAsString(object);
            publishToQueue(json, exchange, queueName, routingKey);
        } catch (JsonProcessingException e) {
            throw new InvalidRequestException(e.getMessage());
        }
    }

    /**
     * Publish to Print module on the exchange with the routing Key on topic exchange
     */
    @Override
    public void publishToQueue(String message, String exchange, String queueName, String routingKey) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(host);
        factory.setUsername(username);
        factory.setPassword(password);
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(exchange, "topic", true);
            BasicProperties props = new BasicProperties("text/json", "utf-8", null, null, null, null, null, null, null,
                    null, null, null, null, null);
            channel.basicPublish(exchange, routingKey, props, message.getBytes());
            log.info("RMQ: Sent '" + message + "'");
        } catch (Exception e) {
            log.error(e.toString());
        }
    }
}
