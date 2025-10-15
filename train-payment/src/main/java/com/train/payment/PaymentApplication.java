package com.train.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 支付服务启动类
 */
@SpringBootApplication(scanBasePackages = {"com.train.payment", "com.train.common"})
@EnableDiscoveryClient
public class PaymentApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
    }
}
