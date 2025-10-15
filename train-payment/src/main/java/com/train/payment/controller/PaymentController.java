package com.train.payment.controller;

import com.train.common.result.Result;
import com.train.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

/**
 * 支付控制器
 */
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {
    
    private final PaymentService paymentService;
    
    /**
     * 创建支付订单
     */
    @PostMapping("/create")
    public Result<String> createPayment(@RequestParam String orderNo, @RequestParam BigDecimal amount) {
        String paymentNo = paymentService.createPayment(orderNo, amount);
        return Result.success(paymentNo);
    }
    
    /**
     * 查询支付状态
     */
    @GetMapping("/status/{paymentNo}")
    public Result<Boolean> queryPaymentStatus(@PathVariable String paymentNo) {
        boolean status = paymentService.queryPaymentStatus(paymentNo);
        return Result.success(status);
    }
}
