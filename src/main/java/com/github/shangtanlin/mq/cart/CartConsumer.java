package com.github.shangtanlin.mq.cart;

import com.github.shangtanlin.mapper.CartItemMapper;
import com.github.shangtanlin.model.entity.cart.CartItem;
import com.rabbitmq.client.Channel;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

import static com.github.shangtanlin.config.mq.CartMQConfig.CART_QUEUE;

@Service
@Slf4j
public class CartConsumer {

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private CartItemMapper cartItemMapper;

    @RabbitListener(queues = CART_QUEUE)
    public void handleCartWriteBack(CartWriteBackMessage cartWriteBackMessage,
                                    Channel channel,
                                    Message message) throws IOException {
        // 1. 获取消息的唯一标识
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        try {
            log.info("收到handleCartWriteBack message:{}", cartWriteBackMessage);

            // 2. 参数校验
            if (cartWriteBackMessage == null || cartWriteBackMessage.getType() == null) {
                // 如果数据本身有问题，直接确认并丢弃，防止死循环
                channel.basicAck(deliveryTag, false);
                return;
            }

            // 3. 执行业务逻辑
            Integer type = cartWriteBackMessage.getType();
            if (type == 1) {
                handleUpdate(cartWriteBackMessage);
            } else if (type == 2) {
                handleDelete(cartWriteBackMessage);
            } else if (type == 3) {
                handleClean(cartWriteBackMessage.getUserId());
            }

            // 4. 业务执行成功，手动 ACK
            channel.basicAck(deliveryTag, false);
            log.info("消息处理成功并已签收: {}", deliveryTag);

        } catch (Exception e) {
            log.error("处理购物车消息失败，准备回滚或重试: {}", e.getMessage());

            // 5. 业务执行失败，拒绝签收
            // 第三个参数 requeue: true 代表放回队列重试；false 代表丢弃（通常配合死信队列使用）
            channel.basicNack(deliveryTag, false, true);
        }

    }

    //更新数据
    private void handleUpdate(CartWriteBackMessage cartWriteBackMessage) {
        CartItem cartItem = new CartItem();
        cartItem.setChecked(cartWriteBackMessage.getCartRedisJson().getChecked());
        cartItem.setQuantity(cartWriteBackMessage.getCartRedisJson().getQuantity());
        cartItem.setUserId(cartWriteBackMessage.getUserId());
        cartItem.setSkuId(cartWriteBackMessage.getSkuId());
        //不管有没有数据都删除，再重新插入
        int delete = cartItemMapper.delete(cartWriteBackMessage.getUserId(), cartWriteBackMessage.getSkuId());
        cartItemMapper.insert(cartItem);
    }


    //删除数据
    private void handleDelete(CartWriteBackMessage cartWriteBackMessage) {
        cartItemMapper.delete(cartWriteBackMessage.getUserId(),cartWriteBackMessage.getSkuId());
    }


    //清空数据
    private void handleClean(Long userId) {
        cartItemMapper.deleteAll(userId);
    }

}
