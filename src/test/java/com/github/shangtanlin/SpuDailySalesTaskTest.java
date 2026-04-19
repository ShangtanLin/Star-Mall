package com.github.shangtanlin;

import com.github.shangtanlin.task.SpuDailySalesTask;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class SpuDailySalesTaskTest {

    @Autowired
    private SpuDailySalesTask spuDailySalesTask;

    @Test
    public void testBackupDailySales() {
        // 手动触发定时任务
        spuDailySalesTask.backupDailySales();

        System.out.println("定时任务执行完成，请检查数据库backup表");
    }
}