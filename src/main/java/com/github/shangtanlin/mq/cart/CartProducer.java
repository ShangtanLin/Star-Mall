package com.github.shangtanlin.mq.cart;

import com.github.shangtanlin.model.redis.CartRedisJson;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.github.shangtanlin.config.mq.CartMQConfig.CART_EXCHANGE;
import static com.github.shangtanlin.config.mq.CartMQConfig.CART_ROUTING_KEY;

@Service
@Slf4j
public class CartProducer {
    @Resource
    private RabbitTemplate rabbitTemplate;

    public void sendCartMessage(Long userId, Long skuId,
                                Integer type, CartRedisJson cartRedisJson) {
        CartWriteBackMessage cartWriteBackMessage =
                new CartWriteBackMessage(userId, skuId, type, cartRedisJson);
        rabbitTemplate.convertAndSend(
                CART_EXCHANGE,
                CART_ROUTING_KEY,
                cartWriteBackMessage
        );
    }
}
