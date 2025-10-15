package com.train.payment.service;

import java.math.BigDecimal;

/**
 * 支付服务接口
 */
public interface PaymentService {
    
    /**
     * 创建支付订单
     */
    String createPayment(String orderNo, BigDecimal amount);
    
    /**
     * 查询支付状态
     */
    boolean queryPaymentStatus(String paymentNo);
}
