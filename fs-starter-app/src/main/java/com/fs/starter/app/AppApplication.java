package com.fs.starter.app;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 用户端启动类（微信小程序）
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.fs.starter")
@MapperScan("com.fs.starter.mapper")
public class AppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AppApplication.class, args);
    }
}
