package com.github.shangtanlin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Hello world!
 *
 */
@SpringBootApplication
@EnableScheduling  //启用定时任务
public class MallApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(MallApplication.class, args);
    }
}
