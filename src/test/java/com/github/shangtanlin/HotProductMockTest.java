package com.github.shangtanlin;

import com.github.shangtanlin.mapper.SpuDailySalesMapper;
import com.github.shangtanlin.mapper.SpuMapper;
import com.github.shangtanlin.model.entity.product.Spu;
import com.github.shangtanlin.model.vo.ProductCardVO;
import com.github.shangtanlin.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class HotProductMockTest {

    @Mock
    private StringRedisTemplate stringRedisTemplate;

    @Mock
    private ZSetOperations<String, String> zSetOperations;

    @Mock
    private SpuMapper spuMapper;

    @Mock
    private SpuDailySalesMapper spuDailySalesMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(stringRedisTemplate.opsForZSet()).thenReturn(zSetOperations);
    }

    @Test
    public void testGetHotProductsToday_FallbackToBackup() {
        // 1. Mock Redis返回少量数据（触发fallback）
        when(zSetOperations.size(anyString())).thenReturn(5L);  // 只5条，不足10条

        // 2. Mock backup表返回TOP10的spuId
        List<Long> backupSpuIds = Arrays.asList(2001L, 2002L, 2003L, 2004L, 2005L,
                                                  2006L, 2007L, 2008L, 2009L, 2010L);
        when(spuDailySalesMapper.selectTop10SpuIds()).thenReturn(backupSpuIds);

        // 3. Mock spu表返回商品详情
        List<Spu> mockSpus = createMockSpus();
        when(spuMapper.selectHotByIds(backupSpuIds)).thenReturn(mockSpus);

        // 4. 调用方法
        List<ProductCardVO> result = productService.getHotProductsToday();

        // 5. 验证结果
        assertNotNull(result);
        assertEquals(10, result.size());

        // 验证排名顺序（2001销量最高，排第一）
        assertEquals(2001L, result.get(0).getSpuId());
        assertEquals(2002L, result.get(1).getSpuId());

        // 6. 验证调用链
        verify(zSetOperations).size("spu:sales:rank");
        verify(spuDailySalesMapper).selectTop10SpuIds();
        verify(spuMapper).selectHotByIds(backupSpuIds);

        // 打印结果
        System.out.println("===== Mock测试结果 =====");
        for (int i = 0; i < result.size(); i++) {
            ProductCardVO product = result.get(i);
            System.out.println(String.format("排名%d: spuId=%d, name=%s, sales=%d",
                    i + 1, product.getSpuId(), product.getSpuName(), product.getSales()));
        }
    }

    @Test
    public void testGetHotProductsToday_FromRedis() {
        // 1. Mock Redis返回充足数据（>10条）
        when(zSetOperations.size(anyString())).thenReturn(15L);

        // 2. Mock Redis ZSet返回TOP10的spuId（按销量降序）
        when(zSetOperations.reverseRange(anyString(), eq(0L), eq(9L)))
                .thenReturn(createRedisTop10());

        // 3. Mock spu表返回商品详情
        List<Long> redisSpuIds = Arrays.asList(3001L, 3002L, 3003L, 3004L, 3005L,
                                                 3006L, 3007L, 3008L, 3009L, 3010L);
        List<Spu> mockSpus = createMockSpusForRedis();
        when(spuMapper.selectHotByIds(anyList())).thenReturn(mockSpus);

        // 4. 调用方法
        List<ProductCardVO> result = productService.getHotProductsToday();

        // 5. 验证结果
        assertNotNull(result);
        assertEquals(10, result.size());

        // 验证是Redis数据（spuId从3001开始）
        assertEquals(3001L, result.get(0).getSpuId());

        // 6. 验证调用链（不调用backup表）
        verify(zSetOperations).size("spu:sales:rank");
        verify(zSetOperations).reverseRange("spu:sales:rank", 0L, 9L);
        verify(spuDailySalesMapper, never()).selectTop10SpuIds();  // 不调用backup

        System.out.println("===== Mock测试结果(Redis) =====");
        for (int i = 0; i < result.size(); i++) {
            ProductCardVO product = result.get(i);
            System.out.println(String.format("排名%d: spuId=%d, name=%s, sales=%d",
                    i + 1, product.getSpuId(), product.getSpuName(), product.getSales()));
        }
    }

    // 创建backup表的Mock商品数据
    private List<Spu> createMockSpus() {
        return Arrays.asList(
                createSpu(2001L, "商品A", 150, new BigDecimal("99.00")),
                createSpu(2002L, "商品B", 120, new BigDecimal("199.00")),
                createSpu(2003L, "商品C", 98, new BigDecimal("59.00")),
                createSpu(2004L, "商品D", 85, new BigDecimal("129.00")),
                createSpu(2005L, "商品E", 72, new BigDecimal("88.00")),
                createSpu(2006L, "商品F", 65, new BigDecimal("168.00")),
                createSpu(2007L, "商品G", 58, new BigDecimal("78.00")),
                createSpu(2008L, "商品H", 45, new BigDecimal("259.00")),
                createSpu(2009L, "商品I", 38, new BigDecimal("49.00")),
                createSpu(2010L, "商品J", 30, new BigDecimal("199.00"))
        );
    }

    // 创建Redis的Mock商品数据
    private List<Spu> createMockSpusForRedis() {
        return Arrays.asList(
                createSpu(3001L, "今日热门A", 200, new BigDecimal("99.00")),
                createSpu(3002L, "今日热门B", 180, new BigDecimal("199.00")),
                createSpu(3003L, "今日热门C", 160, new BigDecimal("59.00")),
                createSpu(3004L, "今日热门D", 140, new BigDecimal("129.00")),
                createSpu(3005L, "今日热门E", 120, new BigDecimal("88.00")),
                createSpu(3006L, "今日热门F", 100, new BigDecimal("168.00")),
                createSpu(3007L, "今日热门G", 90, new BigDecimal("78.00")),
                createSpu(3008L, "今日热门H", 80, new BigDecimal("259.00")),
                createSpu(3009L, "今日热门I", 70, new BigDecimal("49.00")),
                createSpu(3010L, "今日热门J", 60, new BigDecimal("199.00"))
        );
    }

    private Spu createSpu(Long id, String name, Integer sales, BigDecimal minPrice) {
        Spu spu = new Spu();
        spu.setId(id);
        spu.setName(name);
        spu.setSales(sales);
        spu.setMinPrice(minPrice);
        spu.setMainImage("http://example.com/image.jpg");
        spu.setDescription("测试商品描述");
        return spu;
    }

    // 创建Redis ZSet返回的TOP10 spuId集合
    private java.util.Set<String> createRedisTop10() {
        java.util.Set<String> set = new java.util.LinkedHashSet<>();
        set.add("3001");
        set.add("3002");
        set.add("3003");
        set.add("3004");
        set.add("3005");
        set.add("3006");
        set.add("3007");
        set.add("3008");
        set.add("3009");
        set.add("3010");
        return set;
    }
}