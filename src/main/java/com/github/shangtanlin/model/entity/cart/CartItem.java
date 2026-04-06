package com.github.shangtanlin.model.entity.cart;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CartItem {

    private Long id;

    private Long skuId;

    private Long userId;

    private Integer checked; //存入redis

    private Integer quantity; //存入redis

    private LocalDateTime createTime; //存入redis

    private LocalDateTime updateTime;

}
