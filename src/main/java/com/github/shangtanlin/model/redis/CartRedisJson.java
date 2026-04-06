package com.github.shangtanlin.model.redis;

import lombok.Data;

import java.time.LocalDateTime;
//hash的value字段
@Data
public class CartRedisJson {

    private Integer quantity;

    private Integer checked; //默认选中

    private LocalDateTime createTime;
}
