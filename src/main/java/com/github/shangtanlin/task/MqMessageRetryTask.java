package com.github.shangtanlin.task;

import com.alibaba.fastjson.JSON;
import com.github.shangtanlin.mapper.mq.MqMessageLogMapper;
import com.github.shangtanlin.model.dto.mq.MqCorrelationData;
import com.github.shangtanlin.model.entity.mq.MqMessageLog;
import com.github.shangtanlin.mq.cart.CartWriteBackMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.MessageDeliveryMode;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 发送失败消息补偿重发定时任务
 * 处理 type=0（发送失败）且 status IN (0, 2, 3) 的记录
 */
@Component
@Slf4j
public class MqMessageRetryTask {

    /**
     * 最大重试次数
     */
    private static final int MAX_RETRY_COUNT = 10;

    @Autowired
    private MqMessageLogMapper mqMessageLogMapper;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 每分钟扫描一次待重试的发送失败消息
     */
    @Scheduled(cron = "0 0/1 * * * ?")
    public void retrySendMessages() {
        List<MqMessageLog> failLogs = mqMessageLogMapper.selectPendingRetry();

        if (CollectionUtils.isEmpty(failLogs)) {
            return;
        }

        log.info("定时任务开始补偿发送 MQ 消息，共 {} 条", failLogs.size());

        for (MqMessageLog failLog : failLogs) {
            try {
                // 1. 检查重试次数，达到上限则标记为人工处理
                if (failLog.getRetryCount() >= MAX_RETRY_COUNT) {
                    mqMessageLogMapper.markManualProcessed(failLog.getId());
                    log.error("消息 ID: {} 重试次数已达上限({})，转为人工处理！", failLog.getId(), MAX_RETRY_COUNT);
                    continue;
                }

                // 2. 将数据库中的 JSON 字符串还原为业务对象
                CartWriteBackMessage messageObj = JSON.parseObject(
                        failLog.getPayload(),
                        CartWriteBackMessage.class
                );

                // 3. 构造 CorrelationData
                MqCorrelationData cd = new MqCorrelationData(
                        failLog.getId(),
                        failLog.getExchange(),
                        failLog.getRoutingKey(),
                        messageObj
                );

                // 4. 更新数据库：重试次数+1，状态改为发送中，设置下次重试时间
                int newRetryCount = failLog.getRetryCount() + 1;
                LocalDateTime nextRetryTime = LocalDateTime.now().plusMinutes(newRetryCount);
                failLog.setStatus(0);  // 重置为发送中
                failLog.setRetryCount(newRetryCount);
                failLog.setNextRetryTime(nextRetryTime);
                mqMessageLogMapper.updateRetryInfo(failLog);

                // 5. 重新投递
                rabbitTemplate.convertAndSend(
                        failLog.getExchange(),
                        failLog.getRoutingKey(),
                        messageObj,
                        message -> {
                            message.getMessageProperties().setCorrelationId(failLog.getId());
                            message.getMessageProperties().setDeliveryMode(MessageDeliveryMode.PERSISTENT);
                            return message;
                        },
                        cd
                );

                log.info("已触发消息补发，ID: {}, 当前重试次数: {}", failLog.getId(), newRetryCount);

            } catch (Exception e) {
                log.error("重试发送消息发生异常，ID: {}", failLog.getId(), e);
            }
        }
    }

    /**
     * 每小时清理所有成功的消息记录
     */
    @Scheduled(cron = "0 0 * * * ?")
    public void cleanSuccessRecords() {
        int deleted = mqMessageLogMapper.deleteSuccessAll();
        if (deleted > 0) {
            log.info("清理成功消息记录，删除 {} 条", deleted);
        }
    }
}