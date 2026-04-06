package com.github.shangtanlin.common.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {
    // 电商系统最通用的时间格式
    public static final String NORM_DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";

    // 预加载格式化器，提升性能
    private static final DateTimeFormatter DATETIME_FORMATTER =
            DateTimeFormatter.ofPattern(NORM_DATETIME_PATTERN);

    /**
     * 将 LocalDateTime 格式化为字符串
     */
    public static String format(LocalDateTime time) {
        if (time == null) {
            return "";
        }
        return DATETIME_FORMATTER.format(time);
    }
}
