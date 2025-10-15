package com.train.ticket;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 车票服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.train.ticket", "com.train.common"})
@EnableDiscoveryClient
@MapperScan("com.train.ticket.mapper")
public class TicketApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(TicketApplication.class, args);
    }
}
