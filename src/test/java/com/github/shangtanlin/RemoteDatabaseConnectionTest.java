package com.github.shangtanlin;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * 测试阿里云MySQL数据库连接
 */
@SpringBootTest
@Slf4j
public class RemoteDatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    public void testConnection() {
        log.info("========== 开始测试数据库连接 ==========");

        try (Connection connection = dataSource.getConnection()) {
            log.info("数据库连接成功！");
            log.info("连接信息: {}", connection.getMetaData().getURL());
            log.info("数据库版本: {}", connection.getMetaData().getDatabaseProductName() + " " + connection.getMetaData().getDatabaseProductVersion());

            // 测试查询
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery("SELECT 1 as test");

            if (rs.next()) {
                log.info("测试查询成功，结果: {}", rs.getInt("test"));
            }

            // 查看有哪些表
            ResultSet tables = connection.getMetaData().getTables(null, null, "%", new String[]{"TABLE"});
            log.info("\n数据库中的表:");
            int tableCount = 0;
            while (tables.next()) {
                log.info("  - {}", tables.getString("TABLE_NAME"));
                tableCount++;
            }
            log.info("共计 {} 张表", tableCount);

            log.info("========== 测试完成，数据库连接正常 ==========");

        } catch (Exception e) {
            log.error("========== 数据库连接失败 ==========");
            log.error("错误信息: {}", e.getMessage());
            log.error("请检查: 1.服务器IP是否正确 2.端口3306是否开放 3.用户名密码是否正确 4.数据库是否已创建");
            throw new RuntimeException("数据库连接失败", e);
        }
    }
}