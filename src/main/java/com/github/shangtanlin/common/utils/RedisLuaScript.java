package com.github.shangtanlin.common.utils;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Component;
//将lua脚本加载成RedisScript
@Component
public class RedisLuaScript {
    public static final RedisScript<Long> SECKILL_SCRIPT;

    static {
        SECKILL_SCRIPT = RedisScript.of(new ClassPathResource("lua/seckill.lua"),Long.class);
    }
}
