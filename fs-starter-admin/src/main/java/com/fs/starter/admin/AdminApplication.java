package com.fs.starter.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * 后台管理端启动类
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.fs.starter")
@MapperScan("com.fs.starter.mapper")
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
    }
}
