package com.github.shangtanlin.config;

import com.github.shangtanlin.mapper.SkuMapper;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

import static com.github.shangtanlin.common.constant.RedisConstant.SKU_BLOOM_FILTER_KEY;

@Configuration
@Slf4j
public class BloomFilterConfig {

    @Autowired
    private SkuMapper skuMapper;


    @Bean
    public RBloomFilter<String> skuBloomFilter(RedissonClient redissonClient) {
        // 1. 定义名称
        RBloomFilter<String> bloomFilter = redissonClient.getBloomFilter(SKU_BLOOM_FILTER_KEY);

        // 2. 初始化：预期 100万数据，1% 误判率
        // 注意：tryInit 仅在过滤器不存在时生效
        bloomFilter.tryInit(1000000L, 0.01);

        return bloomFilter;
    }


    /**
     * 项目启动后执行：预热布隆过滤器
     */
    @Bean
    public CommandLineRunner bloomFilterInit(RBloomFilter<String> skuBloomFilter) {
        return args -> {
            // 1. 从数据库查出所有 skuId (只查 ID 字段，减轻压力)
            List<Long> skuIds = skuMapper.findAllSkuIds();

            // 2. 批量写入。注意：由于布隆过滤器不支持一次性 addAll，建议循环写入
            // 如果数据量巨大，可以考虑开启多线程或者流处理
            skuIds.forEach(id -> skuBloomFilter.add(id.toString()));

            log.info("布隆过滤器预热完成，共载入 {} 条数据", skuIds.size());
        };
    }
}
