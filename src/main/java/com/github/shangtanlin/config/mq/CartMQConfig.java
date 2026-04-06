package com.github.shangtanlin.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CartMQConfig {
    public static final String CART_EXCHANGE = "cart.write.back.exchange";
    public static final String CART_QUEUE = "cart.write.back.queue";
    public static final String CART_ROUTING_KEY = "cart.write.back";

    @Bean
    public DirectExchange cartExchange() {
        return new DirectExchange(CART_EXCHANGE);
    }

    @Bean
    public Queue cartQueue() {
        return new Queue(CART_QUEUE);
    }

    @Bean
    public Binding cartBinding() {
        return BindingBuilder
                .bind(cartQueue())
                .to(cartExchange())
                .with(CART_ROUTING_KEY);
    }

}
