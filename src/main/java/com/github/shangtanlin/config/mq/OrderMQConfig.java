package com.github.shangtanlin.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * 超时关掉配置
 */
@Configuration
public class OrderMQConfig {

    // 1. 定义常量，方便以后维护
    public static final String ORDER_DELAY_EXCHANGE = "order.delay.exchange";
    public static final String ORDER_DELAY_QUEUE = "order.delay.queue";
    public static final String ORDER_DELAY_ROUTING_KEY = "order.delay.key";

    @Bean
    public Queue orderDelayQueue() {
        return new Queue(ORDER_DELAY_QUEUE, true);
    }

    @Bean
    public CustomExchange orderDelayExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type", "direct");
        return new CustomExchange(ORDER_DELAY_EXCHANGE, "x-delayed-message", true, false, args);
    }

    /**
     * 核心修改点：在 Queue 参数前加上 @Qualifier
     */
    @Bean
    public Binding orderDelayBinding(
            @Qualifier("orderDelayQueue") Queue orderDelayQueue,
            @Qualifier("orderDelayExchange") CustomExchange orderDelayExchange) {

        return BindingBuilder.bind(orderDelayQueue)
                .to(orderDelayExchange)
                .with(ORDER_DELAY_ROUTING_KEY)
                .noargs();
    }
}