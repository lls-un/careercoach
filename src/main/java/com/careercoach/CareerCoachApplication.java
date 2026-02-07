package com.careercoach;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.careercoach.mapper") // 扫描Mapper接口
public class CareerCoachApplication {

    public static void main(String[] args) {
        SpringApplication.run(CareerCoachApplication.class, args);
    }
}