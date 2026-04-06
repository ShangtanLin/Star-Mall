package com.github.shangtanlin.config.mq;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class SubOrderMQConfig {

    /**
     * 交换机名称：订单业务交换机
     */
    public static final String SUB_ORDER_EXCHANGE = "order.sub.exchange";

    /**
     * 队列名称：同步 ES 索引的专用队列
     */
    public static final String SUB_ORDER_ES_QUEUE = "order.sub.es.queue";

    /**
     * 路由键：用于匹配子订单同步逻辑
     * 使用 Topic 模式建议：order.sub.sync
     */
    public static final String SUB_ORDER_SYNC_ROUTING_KEY = "order.sub.sync";


    /**
     * 1. 声明交换机 (TopicExchange 模式最灵活)
     */
    @Bean
    public TopicExchange subOrderExchange() {
        // 参数：名称，是否持久化，是否自动删除
        return new TopicExchange(SUB_ORDER_EXCHANGE, true, false);
    }

    /**
     * 2. 声明队列
     */
    @Bean
    public Queue subOrderEsQueue() {
        // 参数：名称，是否持久化
        return new Queue(SUB_ORDER_ES_QUEUE, true);
    }

    /**
     * 3. 绑定队列到交换机，并指定路由键
     */
    @Bean
    public Binding subOrderEsBinding() {
        return BindingBuilder
                .bind(subOrderEsQueue())
                .to(subOrderExchange())
                .with(SUB_ORDER_SYNC_ROUTING_KEY);
    }

    /**
     * 4. 配置消息转换器：将对象自动转为 JSON 格式发送
     */
    @Bean
    public MessageConverter subOrderMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
