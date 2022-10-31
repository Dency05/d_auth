package com.example.sm.common.config;

import com.example.sm.common.decorator.NullAwareBeanUtilsBean;
import com.example.sm.common.decorator.RequestSession;
import com.example.sm.common.decorator.Response;
import com.example.sm.common.decorator.GeneralHelper;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.RabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.boot.web.client.RootUriTemplateHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

@Configuration
public class GeneralBeans {
    @Value("${trs.defaults.prefetchCount}")
    String prefetchCount;

    @Value("${trs.defaults.concurrentConsumers}")
    String concurrentConsumers;

    @Bean
    ModelMapper getModelMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
        return modelMapper;
    }

    @Bean
    public GeneralHelper getGeneralHelper(){
        return new GeneralHelper();
    }

    @Bean
    public Response getResponse() {
        return new Response();
    }

    @Bean
    public RequestSession getRequestSession(){
        return new RequestSession();
    }

    @Bean
    public NullAwareBeanUtilsBean beanUtilsBean(){
        return new NullAwareBeanUtilsBean();
    }

    @Bean
    public RabbitListenerContainerFactory<SimpleMessageListenerContainer> prefetchTenRabbitListenerContainerFactory(ConnectionFactory rabbitConnectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(rabbitConnectionFactory);
        factory.setPrefetchCount(Integer.valueOf(prefetchCount));
        factory.setConcurrentConsumers(Integer.valueOf(concurrentConsumers));
        return factory;
    }

    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }

    }

