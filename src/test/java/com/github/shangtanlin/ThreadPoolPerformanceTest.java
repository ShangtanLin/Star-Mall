package com.github.shangtanlin;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.shangtanlin.mapper.*;
import com.github.shangtanlin.model.entity.order.OrderItem;
import com.github.shangtanlin.model.entity.order.SubOrder;
import com.github.shangtanlin.model.entity.product.Sku;
import com.github.shangtanlin.model.entity.product.Spu;
import com.github.shangtanlin.model.entity.shop.Shop;
import com.github.shangtanlin.model.entity.category.Category;
import com.github.shangtanlin.model.entity.Brand;
import com.github.shangtanlin.model.vo.order.SubOrderVO;
import com.github.shangtanlin.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 线程池优化效果对比测试
 * 测试 getSubOrderDetail 方法的并行查询优化效果
 */
@SpringBootTest
@Slf4j
public class ThreadPoolPerformanceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SubOrderMapper subOrderMapper;

    @Autowired
    private OrderItemMapper orderItemMapper;

    @Autowired
    private ShopMapper shopMapper;

    @Autowired
    private SpuMapper spuMapper;

    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private BrandMapper brandMapper;

    @Autowired
    @Qualifier("orderQueryExecutor")
    private ThreadPoolExecutor orderQueryExecutor;

    @Autowired
    private DataSource dataSource;

    // 测试用的子订单编号和商品ID
    private static String TEST_ORDER_SN = null;
    private static Long TEST_USER_ID = 1L;
    private static Long TEST_SPU_ID = null;

    /**
     * 初始化测试数据：从数据库中查找一个真实存在的子订单和商品
     */
    @BeforeAll
    public static void setup(@Autowired SubOrderMapper subOrderMapper, @Autowired SpuMapper spuMapper) {
        // 使用 selectList + LIMIT 1 避免 selectOne 报错
        List<SubOrder> subOrders = subOrderMapper.selectList(
                new LambdaQueryWrapper<SubOrder>().last("LIMIT 1")
        );
        if (!subOrders.isEmpty()) {
            SubOrder subOrder = subOrders.get(0);
            TEST_ORDER_SN = subOrder.getSubOrderSn();
            TEST_USER_ID = subOrder.getUserId();
            log.info("测试数据初始化成功: orderSn={}, userId={}", TEST_ORDER_SN, TEST_USER_ID);
        }

        List<Spu> spus = spuMapper.selectList(
                new LambdaQueryWrapper<Spu>().last("LIMIT 1")
        );
        if (!spus.isEmpty()) {
            TEST_SPU_ID = spus.get(0).getId();
            log.info("商品数据初始化成功: spuId={}", TEST_SPU_ID);
        }
    }

    /**
     * 测试优化后的并行版本
     */
    @Test
    public void testParallelVersion() {
        if (TEST_ORDER_SN == null) {
            log.warn("没有找到测试数据，请先在数据库中创建订单");
            return;
        }

        // 预热
        orderService.getSubOrderDetail(TEST_ORDER_SN, TEST_USER_ID);

        int iterations = 20;
        long totalTime = 0;

        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            orderService.getSubOrderDetail(TEST_ORDER_SN, TEST_USER_ID);
            totalTime += System.currentTimeMillis() - start;
        }

        log.info("===== 并行版本测试结果 =====");
        log.info("总耗时: {}ms, 平均耗时: {}ms, 测试次数: {}", totalTime, totalTime / iterations, iterations);
    }

    /**
     * 测试串行版本（模拟优化前的代码）
     */
    @Test
    public void testSerialVersion() {
        if (TEST_ORDER_SN == null) {
            log.warn("没有找到测试数据，请先在数据库中创建订单");
            return;
        }

        // 预热
        serialGetDetail(TEST_ORDER_SN, TEST_USER_ID);

        int iterations = 20;
        long totalTime = 0;

        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            serialGetDetail(TEST_ORDER_SN, TEST_USER_ID);
            totalTime += System.currentTimeMillis() - start;
        }

        log.info("===== 串行版本测试结果 =====");
        log.info("总耗时: {}ms, 平均耗时: {}ms, 测试次数: {}", totalTime, totalTime / iterations, iterations);
    }

    /**
     * 串行版本（模拟优化前的代码）
     */
    private void serialGetDetail(String orderSn, Long userId) {
        // 1. 查询子订单
        SubOrder subOrder = subOrderMapper.selectOne(new LambdaQueryWrapper<SubOrder>()
                .eq(SubOrder::getSubOrderSn, orderSn)
                .eq(SubOrder::getUserId, userId));
        if (subOrder == null) return;

        // 2. 查询店铺
        Shop shop = shopMapper.selectById(subOrder.getShopId());

        // 3. 查询订单项
        List<OrderItem> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>()
                        .eq(OrderItem::getSubOrderId, subOrder.getId())
        );
    }

    /**
     * 一键对比测试
     */
    @Test
    public void testCompareBoth() {
        if (TEST_ORDER_SN == null) {
            log.warn("没有找到测试数据，请先在数据库中创建订单");
            return;
        }

        log.info("\n========== 开始性能对比测试 ==========");
        log.info("测试订单: orderSn={}, userId={}", TEST_ORDER_SN, TEST_USER_ID);

        // 预热
        log.info("预热阶段...");
        orderService.getSubOrderDetail(TEST_ORDER_SN, TEST_USER_ID);
        serialGetDetail(TEST_ORDER_SN, TEST_USER_ID);

        int iterations = 20;

        // 测试并行版本
        long parallelTotalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            SubOrderVO vo = orderService.getSubOrderDetail(TEST_ORDER_SN, TEST_USER_ID);
            parallelTotalTime += System.currentTimeMillis() - start;
        }

        // 测试串行版本
        long serialTotalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            serialGetDetail(TEST_ORDER_SN, TEST_USER_ID);
            serialTotalTime += System.currentTimeMillis() - start;
        }

        long parallelAvg = parallelTotalTime / iterations;
        long serialAvg = serialTotalTime / iterations;

        log.info("\n========== 测试结果对比 ==========");
        log.info("串行版本平均耗时: {}ms", serialAvg);
        log.info("并行版本平均耗时: {}ms", parallelAvg);

        if (serialAvg > parallelAvg) {
            long improvement = serialAvg - parallelAvg;
            double percentage = (double) improvement * 100 / serialAvg;
            log.info("优化提升: {}ms ({:.2f}%)", improvement, percentage);
        } else {
            log.info("优化效果不明显，可能与数据库响应速度有关");
        }

        log.info("========== 测试结束 ==========\n");
    }

    /**
     * 测试6个并行查询的场景（模拟商品详情页查询）
     * 这种场景下线程池优化应该有明显效果
     */
    @Test
    public void testSixParallelQueries() {
        if (TEST_SPU_ID == null) {
            log.warn("没有找到商品数据，无法进行测试");
            return;
        }

        log.info("\n========== 开始测试6个并行查询场景 ==========");
        log.info("测试商品ID: {}", TEST_SPU_ID);

        // 预热
        log.info("预热阶段...");
        serialGetProductDetail(TEST_SPU_ID);
        parallelGetProductDetail(TEST_SPU_ID);

        int iterations = 20;

        // 测试串行版本
        long serialTotalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            serialGetProductDetail(TEST_SPU_ID);
            serialTotalTime += System.currentTimeMillis() - start;
        }

        // 测试并行版本
        long parallelTotalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            parallelGetProductDetail(TEST_SPU_ID);
            parallelTotalTime += System.currentTimeMillis() - start;
        }

        long serialAvg = serialTotalTime / iterations;
        long parallelAvg = parallelTotalTime / iterations;

        log.info("\n========== 6个查询测试结果 ==========");
        log.info("串行版本平均耗时: {}ms", serialAvg);
        log.info("并行版本平均耗时: {}ms", parallelAvg);

        if (serialAvg > parallelAvg) {
            long improvement = serialAvg - parallelAvg;
            double percentage = (double) improvement * 100 / serialAvg;
            log.info("优化提升: {}ms ({:.2f}%)", improvement, percentage);
            log.info("✅ 线程池优化有效！");
        } else {
            log.info("优化效果不明显");
        }

        log.info("========== 测试结束 ==========\n");
    }

    /**
     * 串行版本：模拟商品详情页查询（6个查询依次执行）
     */
    private void serialGetProductDetail(Long spuId) {
        // 1. 查询SPU
        Spu spu = spuMapper.selectById(spuId);
        if (spu == null) return;

        // 2. 查询SKU列表
        List<Sku> skus = skuMapper.selectList(
                new LambdaQueryWrapper<Sku>().eq(Sku::getSpuId, spuId)
        );

        // 3. 查询店铺
        Shop shop = shopMapper.selectById(spu.getShopId());

        // 4. 查询分类
        Category category = categoryMapper.selectById(spu.getCategoryId());

        // 5. 查询品牌
        Brand brand = brandMapper.selectById(spu.getBrandId());

        // 6. 查询订单项（模拟销量数据）
        List<OrderItem> orderItems = orderItemMapper.selectList(
                new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getSpuId, spuId).last("LIMIT 10")
        );
    }

    /**
     * 并行版本：模拟商品详情页查询（6个查询并行执行）
     */
    private void parallelGetProductDetail(Long spuId) {
        // 1. 先查询SPU（后续查询依赖它）
        Spu spu = spuMapper.selectById(spuId);
        if (spu == null) return;

        // 2-6. 并行查询剩余5个数据
        CompletableFuture<List<Sku>> skusFuture = CompletableFuture.supplyAsync(
                () -> skuMapper.selectList(new LambdaQueryWrapper<Sku>().eq(Sku::getSpuId, spuId)),
                orderQueryExecutor
        );

        CompletableFuture<Shop> shopFuture = CompletableFuture.supplyAsync(
                () -> shopMapper.selectById(spu.getShopId()),
                orderQueryExecutor
        );

        CompletableFuture<Category> categoryFuture = CompletableFuture.supplyAsync(
                () -> categoryMapper.selectById(spu.getCategoryId()),
                orderQueryExecutor
        );

        CompletableFuture<Brand> brandFuture = CompletableFuture.supplyAsync(
                () -> brandMapper.selectById(spu.getBrandId()),
                orderQueryExecutor
        );

        CompletableFuture<List<OrderItem>> itemsFuture = CompletableFuture.supplyAsync(
                () -> orderItemMapper.selectList(
                        new LambdaQueryWrapper<OrderItem>().eq(OrderItem::getSpuId, spuId).last("LIMIT 10")),
                orderQueryExecutor
        );

        // 等待所有查询完成
        CompletableFuture.allOf(skusFuture, shopFuture, categoryFuture, brandFuture, itemsFuture).join();
    }

    /**
     * 测试复杂SQL查询场景（多表JOIN、聚合统计等）
     * 这种场景下线程池优化应该有明显效果
     */
    @Test
    public void testComplexSqlQueries() {
        log.info("\n========== 开始测试复杂SQL查询场景 ========== ");

        // 预热
        log.info("预热阶段...");
        executeComplexSqlSerial();
        executeComplexSqlParallel();

        int iterations = 10;

        // 测试串行版本
        long serialTotalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            executeComplexSqlSerial();
            serialTotalTime += System.currentTimeMillis() - start;
        }

        // 测试并行版本
        long parallelTotalTime = 0;
        for (int i = 0; i < iterations; i++) {
            long start = System.currentTimeMillis();
            executeComplexSqlParallel();
            parallelTotalTime += System.currentTimeMillis() - start;
        }

        long serialAvg = serialTotalTime / iterations;
        long parallelAvg = parallelTotalTime / iterations;

        log.info("\n========== 复杂SQL查询测试结果 ==========");
        log.info("串行版本平均耗时: {}ms", serialAvg);
        log.info("并行版本平均耗时: {}ms", parallelAvg);

        if (serialAvg > parallelAvg) {
            long improvement = serialAvg - parallelAvg;
            double percentage = (double) improvement * 100 / serialAvg;
            log.info("优化提升: {}ms ({.2f}%)", improvement, String.format("%.2f", percentage));
            log.info("线程池优化有效！");
        } else {
            log.info("优化效果不明显");
        }

        log.info("========== 测试结束 ========== \n");
    }

    /**
     * 串行执行6个复杂SQL查询
     */
    private void executeComplexSqlSerial() {
        try (Connection conn = dataSource.getConnection()) {
            // 1. 店铺销售排行（多表JOIN + 聚合）
            executeQuery(conn, """
                SELECT s.shop_name, COUNT(so.id) as order_count, SUM(so.pay_amount) as total_sales
                FROM shop s
                LEFT JOIN spu sp ON s.id = sp.shop_id
                LEFT JOIN order_item oi ON sp.id = oi.spu_id
                LEFT JOIN sub_order so ON oi.sub_order_id = so.id
                WHERE so.status IN (1, 2, 5)
                GROUP BY s.id, s.shop_name
                ORDER BY total_sales DESC
                LIMIT 10
                """);

            // 2. 商品销售统计（多表JOIN + 聚合）
            executeQuery(conn, """
                SELECT sp.id, sp.name, sp.sales, COUNT(oi.id) as order_item_count,
                       SUM(oi.quantity) as total_quantity, SUM(oi.total_amount) as total_revenue
                FROM spu sp
                LEFT JOIN order_item oi ON sp.id = oi.spu_id
                LEFT JOIN sub_order so ON oi.sub_order_id = so.id
                WHERE so.status IN (1, 2, 5)
                GROUP BY sp.id, sp.name, sp.sales
                ORDER BY total_revenue DESC
                LIMIT 20
                """);

            // 3. 分类商品统计（多表JOIN + 分组）
            executeQuery(conn, """
                SELECT c.name as category_name, COUNT(sp.id) as spu_count,
                       SUM(sp.sales) as category_sales, MIN(sp.min_price) as min_price,
                       MAX(sp.min_price) as max_price
                FROM category c
                LEFT JOIN spu sp ON c.id = sp.category_id
                GROUP BY c.id, c.name
                ORDER BY category_sales DESC
                """);

            // 4. 用户订单统计（多表JOIN + 聚合）
            executeQuery(conn, """
                SELECT po.user_id, COUNT(po.id) as order_count,
                       SUM(po.pay_amount) as total_spent, AVG(po.pay_amount) as avg_order_amount
                FROM parent_order po
                WHERE po.status IN (1, 2, 5)
                GROUP BY po.user_id
                ORDER BY total_spent DESC
                LIMIT 20
                """);

            // 5. SKU库存分析（多表JOIN + 统计）
            executeQuery(conn, """
                SELECT sp.name as spu_name, sk.sku_title, sk.stock, sk.price,
                       sp.sales as spu_sales
                FROM sku sk
                JOIN spu sp ON sk.spu_id = sp.id
                WHERE sk.stock < 100
                ORDER BY sk.stock ASC
                LIMIT 50
                """);

            // 6. 订单状态分布统计
            executeQuery(conn, """
                SELECT so.status, COUNT(so.id) as count,
                       SUM(so.pay_amount) as total_amount,
                       AVG(so.pay_amount) as avg_amount
                FROM sub_order so
                GROUP BY so.status
                ORDER BY count DESC
                """);

        } catch (Exception e) {
            log.error("SQL执行失败: {}", e.getMessage());
        }
    }

    /**
     * 并行执行6个复杂SQL查询
     */
    private void executeComplexSqlParallel() {
        try (Connection conn = dataSource.getConnection()) {
            // 并行执行6个复杂查询
            CompletableFuture<Void> f1 = CompletableFuture.runAsync(
                    () -> executeQuerySafe("""
                        SELECT s.shop_name, COUNT(so.id) as order_count, SUM(so.pay_amount) as total_sales
                        FROM shop s
                        LEFT JOIN spu sp ON s.id = sp.shop_id
                        LEFT JOIN order_item oi ON sp.id = oi.spu_id
                        LEFT JOIN sub_order so ON oi.sub_order_id = so.id
                        WHERE so.status IN (1, 2, 5)
                        GROUP BY s.id, s.shop_name
                        ORDER BY total_sales DESC
                        LIMIT 10
                        """),
                    orderQueryExecutor
            );

            CompletableFuture<Void> f2 = CompletableFuture.runAsync(
                    () -> executeQuerySafe("""
                        SELECT sp.id, sp.name, sp.sales, COUNT(oi.id) as order_item_count,
                               SUM(oi.quantity) as total_quantity, SUM(oi.total_amount) as total_revenue
                        FROM spu sp
                        LEFT JOIN order_item oi ON sp.id = oi.spu_id
                        LEFT JOIN sub_order so ON oi.sub_order_id = so.id
                        WHERE so.status IN (1, 2, 5)
                        GROUP BY sp.id, sp.name, sp.sales
                        ORDER BY total_revenue DESC
                        LIMIT 20
                        """),
                    orderQueryExecutor
            );

            CompletableFuture<Void> f3 = CompletableFuture.runAsync(
                    () -> executeQuerySafe("""
                        SELECT c.name as category_name, COUNT(sp.id) as spu_count,
                               SUM(sp.sales) as category_sales, MIN(sp.min_price) as min_price,
                               MAX(sp.min_price) as max_price
                        FROM category c
                        LEFT JOIN spu sp ON c.id = sp.category_id
                        GROUP BY c.id, c.name
                        ORDER BY category_sales DESC
                        """),
                    orderQueryExecutor
            );

            CompletableFuture<Void> f4 = CompletableFuture.runAsync(
                    () -> executeQuerySafe("""
                        SELECT po.user_id, COUNT(po.id) as order_count,
                               SUM(po.pay_amount) as total_spent, AVG(po.pay_amount) as avg_order_amount
                        FROM parent_order po
                        WHERE po.status IN (1, 2, 5)
                        GROUP BY po.user_id
                        ORDER BY total_spent DESC
                        LIMIT 20
                        """),
                    orderQueryExecutor
            );

            CompletableFuture<Void> f5 = CompletableFuture.runAsync(
                    () -> executeQuerySafe("""
                        SELECT sp.name as spu_name, sk.sku_title, sk.stock, sk.price,
                               sp.sales as spu_sales
                        FROM sku sk
                        JOIN spu sp ON sk.spu_id = sp.id
                        WHERE sk.stock < 100
                        ORDER BY sk.stock ASC
                        LIMIT 50
                        """),
                    orderQueryExecutor
            );

            CompletableFuture<Void> f6 = CompletableFuture.runAsync(
                    () -> executeQuerySafe("""
                        SELECT so.status, COUNT(so.id) as count,
                               SUM(so.pay_amount) as total_amount,
                               AVG(so.pay_amount) as avg_amount
                        FROM sub_order so
                        GROUP BY so.status
                        ORDER BY count DESC
                        """),
                    orderQueryExecutor
            );

            // 等待所有查询完成
            CompletableFuture.allOf(f1, f2, f3, f4, f5, f6).join();

        } catch (Exception e) {
            log.error("并行SQL执行失败: {}", e.getMessage());
        }
    }

    /**
     * 执行SQL查询
     */
    private void executeQuery(Connection conn, String sql) {
        try (PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            // 遍历结果集，模拟消费数据
            int count = 0;
            while (rs.next()) {
                count++;
            }
            log.debug("查询返回 {} 条记录", count);
        } catch (Exception e) {
            log.error("查询失败: {}", e.getMessage());
        }
    }

    /**
     * 安全执行SQL查询（为并行调用封装）
     */
    private void executeQuerySafe(String sql) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            int count = 0;
            while (rs.next()) {
                count++;
            }
            log.debug("查询返回 {} 条记录", count);
        } catch (Exception e) {
            log.error("查询失败: {}", e.getMessage());
        }
    }
}