package com.github.shangtanlin.task;

import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.github.shangtanlin.mapper.SpuDailySalesMapper;
import com.github.shangtanlin.model.entity.product.SpuDailySales;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static com.github.shangtanlin.common.constant.RedisConstant.SPU_SALES_RANK_KEY;

/**
 * 每日热门商品销量备份定时任务
 * 每日零点执行：将Redis中的今日热门TOP10备份到数据库，然后清空Redis
 */
@Component
@Slf4j
public class SpuDailySalesTask {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private SpuDailySalesMapper spuDailySalesMapper;

    /**
     * 每日零点执行
     * cron: 秒 分 时 日 月 周
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void backupDailySales() {
        log.info("====== 开始执行每日热门商品销量备份任务 ======");

        String key = SPU_SALES_RANK_KEY;

        // 1. 从Redis获取今日TOP10
        Set<String> top10SpuIds = stringRedisTemplate.opsForZSet().reverseRange(key, 0, 9);

        if (CollectionUtils.isEmpty(top10SpuIds)) {
            log.info("Redis中无热门商品数据，跳过备份");
            return;
        }

        log.info("Redis TOP10商品数量: {}", top10SpuIds.size());

        // 2. 清空数据库backup表
        spuDailySalesMapper.deleteAll();
        log.info("已清空数据库backup表");

        // 3. 构建备份数据列表
        LocalDate today = LocalDate.now();
        List<SpuDailySales> backupList = new ArrayList<>();

        for (String spuIdStr : top10SpuIds) {
            Long spuId = Long.valueOf(spuIdStr);
            Double score = stringRedisTemplate.opsForZSet().score(key, spuIdStr);

            if (score != null) {
                SpuDailySales record = new SpuDailySales();
                record.setId(IdWorker.getId());  // 生成唯一ID
                record.setSpuId(spuId);
                record.setSales(score.intValue());
                record.setSaleDate(today);
                record.setCreateTime(LocalDateTime.now());
                backupList.add(record);
            }
        }

        // 4. 批量插入backup表
        if (!backupList.isEmpty()) {
            spuDailySalesMapper.batchInsert(backupList);
            log.info("已备份 {} 条热门商品数据到数据库", backupList.size());
        }

        // 5. 清空Redis
        stringRedisTemplate.delete(key);
        log.info("已清空Redis今日销量数据");

        log.info("====== 每日热门商品销量备份任务完成 ======");
    }
}