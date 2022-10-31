package com.example.sm.auth.rabbitmq;


import com.google.gson.Gson;
import org.springframework.amqp.core.Message;

import java.lang.reflect.Type;


public class ConsumerHelper {
    public static <T> T parsePayload(Message message, Class<T> c){
        String messageBody= new String(message.getBody());
        return  new Gson().fromJson(messageBody,c);
    }

    public static <T> T parsePayload(Message message, Type type){
        String messageBody= new String(message.getBody());
        return  new Gson().fromJson(messageBody,type);
    }


}
