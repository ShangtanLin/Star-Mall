package com.github.shangtanlin.model.dto.mq;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.amqp.rabbit.connection.CorrelationData;

import org.springframework.amqp.rabbit.connection.CorrelationData;
import lombok.Data;

@Data
public class MqCorrelationData extends CorrelationData {
    private String exchange; //快照，失败后异步回调能知道发给哪个交换机
    private String routingKey; //快照，失败后异步回调能知道发给哪个交换机
    private Object payload; // 存放原始对象（消息体）


    // 构造方法
    public MqCorrelationData(String id, String exchange, String routingKey, Object payload) {
        super(id); //继承了父类的唯一标识，让Spring能够通过ID匹配到回调信号
        this.exchange = exchange;
        this.routingKey = routingKey;
        this.payload = payload;
    }
}