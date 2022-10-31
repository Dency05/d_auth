package com.example.sm.auth.rabbitmq;

import com.example.sm.auth.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;

@Slf4j
@Service
public class RMQConsumer {

    @Autowired
    UserService userService;

    @Autowired
    UserConsumerService userConsumerService;


    @RabbitListener(
            containerFactory = "prefetchTenRabbitListenerContainerFactory",
            bindings = @QueueBinding(
                    value = @Queue(value = "${rmq.userSend.queueName}",durable = "true"),
                    exchange = @Exchange(value = "${rmq.userSend.exchangeName}",type = "topic"),
                    key = "${rmq.userSend.routingKey}")
    )
    public void processUser(Message message){
        try{
            log.info("Message");
            String id= ConsumerHelper.parsePayload(message, (Type) Object.class);
            userService.sendMessage(id);
            userConsumerService.getUser(id);
        }
        catch (Exception e){
            log.info("Error happen in consuming data");
        }
    }
}
