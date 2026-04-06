package com.github.shangtanlin.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CouponMQConfig {
    // 1. 定义交换机名称
    public static final String COUPON_EXCHANGE = "coupon.exchange";

    // 2. 定义队列名称
    public static final String COUPON_RECEIVE_QUEUE = "coupon.receive.queue";

    // 3. 定义路由键 (Routing Key)
    public static final String COUPON_RECEIVE_KEY = "coupon.receive.key";

    /**
     * 声明交换机
     */
    @Bean
    public DirectExchange couponExchange() {
        // durable: true 代表交换机在 RabbitMQ 服务重启后依然存在
        return new DirectExchange(COUPON_EXCHANGE, true, false);
    }

    /**
     * 声明队列
     */
    @Bean
    public Queue couponReceiveQueue() {
        // durable: true 代表消息会持久化到磁盘，防止丢失
        return new Queue(COUPON_RECEIVE_QUEUE, true);
    }

    /**
     * 声明绑定关系：将队列和交换机通过路由键绑定起来
     */
    @Bean
    public Binding couponReceiveBinding
    (@Qualifier("couponReceiveQueue") Queue couponReceiveQueue,
     @Qualifier("couponExchange")DirectExchange couponExchange) {
        return BindingBuilder.bind(couponReceiveQueue)
                .to(couponExchange)
                .with(COUPON_RECEIVE_KEY);
    }
}
