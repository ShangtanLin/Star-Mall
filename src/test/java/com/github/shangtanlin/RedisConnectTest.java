package com.github.shangtanlin;

import com.github.shangtanlin.mapper.ParentOrderMapper;
import com.github.shangtanlin.model.dto.order.OrderSubmitDTO;
import com.github.shangtanlin.model.entity.order.ParentOrder;
import com.github.shangtanlin.service.OrderService;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
@MapperScan("com.github.shangtanlin.mapper")
public class RedisConnectTest {

    @Autowired
    private ParentOrderMapper parentOrderMapper;

    @Autowired
    private OrderService orderService;

    @Test
    public void test() {
        OrderSubmitDTO orderSubmitDTO = OrderSubmitDTO.builder()
                .addressId(13L)
                .couponUserRecordId(null)
                .submitToken("68eb577a78824e4f93472873976f92d5")
                .source(2)
                .shops(Arrays.asList(
                        // 店铺 1
                        OrderSubmitDTO.ShopOrderSubmitRequest.builder()
                                .shopId(6L)
                                .shopCouponId(null)
                                .items(Collections.singletonList(
                                        OrderSubmitDTO.ItemSubmitRequest.builder()
                                                .skuId(5001L)
                                                .quantity(1)
                                                .build()
                                ))
                                .build(),
                        // 店铺 2
                        OrderSubmitDTO.ShopOrderSubmitRequest.builder()
                                .shopId(1L)
                                .shopCouponId(null)
                                .items(Collections.singletonList(
                                        OrderSubmitDTO.ItemSubmitRequest.builder()
                                                .skuId(5503L)
                                                .quantity(1)
                                                .build()
                                ))
                                .build()
                ))
                .build();

        orderService.submitOrder(orderSubmitDTO);
    }




}
