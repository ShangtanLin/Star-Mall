package com.github.shangtanlin.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;

import java.util.concurrent.*;

/**
 * 线程池配置
 * 用于订单ES同步等并行查询场景
 */
@Configuration
@Slf4j
public class ThreadPoolConfig {

    /**
     * 订单查询线程池
     * 场景：订单详情、ES同步数据组装时的并行查询
     */
    @Bean("orderQueryExecutor")
    public ThreadPoolExecutor orderQueryExecutor() {
        return new ThreadPoolExecutor(
                16,                     // 核心线程数
                16,                     // 最大线程数
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(50),
                new CustomizableThreadFactory("order-query-"),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}