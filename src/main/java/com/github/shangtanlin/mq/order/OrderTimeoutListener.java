package com.github.shangtanlin.mq.order;

import com.github.shangtanlin.config.mq.OrderMQConfig;
import com.github.shangtanlin.model.dto.order.OrderCancelMessage;
import com.github.shangtanlin.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 超时订单消费者
 */
@Component
@RabbitListener(queues = OrderMQConfig.ORDER_DELAY_QUEUE) // 监听 Config 里定义的队列
@Slf4j
public class OrderTimeoutListener {

    @Autowired
    private OrderService orderService;


    @RabbitHandler
    public void onOrderTimeoutMessage(OrderCancelMessage message) {
        // 1. 基础判空校验（防御式编程）
        if (message == null || message.getOrderSn() == null || message.getUserId() == null) {
            log.warn("⏰ [超时关单] 收到无效消息，直接丢弃: {}", message);
            return;
        }

        String orderSn = message.getOrderSn();
        Long userId = message.getUserId();

        log.info("⏰ [超时关单] 开始处理自动关单任务 | 订单号: {} | 用户ID: {}", orderSn, userId);

        try {
            // 2. 调用 Service 执行核心关单逻辑
            // 该方法内部应包含：状态校验、修改状态、回滚库存、回滚优惠券
            boolean isClosed = orderService.cancelOrder(orderSn, userId);

            // 3. 根据处理结果记录日志
            if (isClosed) {
                log.info("✅ [超时关单] 订单 {} 处理成功，已关闭并释放资源", orderSn);
            } else {
                // 说明订单可能已经支付了，或者已经被手动取消了，这属于正常业务跳过
                log.info("ℹ️ [超时关单] 订单 {} 无需处理（可能已支付或已取消）", orderSn);
            }

        } catch (Exception e) {
            log.error("❌ [超时关单] 处理异常，订单号: {}", orderSn, e);

            // 4. 关键：抛出异常触发 RabbitMQ 的重试机制
            // 只有抛出异常，消息才不会被确认，会根据配置进行重试或进入死信队列
            //throw new RuntimeException("自动关单失败，等待 MQ 重试: " + orderSn, e);

        }

    }


}