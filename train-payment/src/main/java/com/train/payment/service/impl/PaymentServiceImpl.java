package com.train.payment.service.impl;

import cn.hutool.core.util.IdUtil;
import com.train.payment.service.PaymentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * 支付服务实现（模拟）
 */
@Slf4j
@Service
public class PaymentServiceImpl implements PaymentService {
    
    @Override
    public String createPayment(String orderNo, BigDecimal amount) {
        // 模拟创建支付订单
        String paymentNo = "PAY" + IdUtil.getSnowflakeNextIdStr();
        log.info("创建支付订单 - 订单号: {}, 金额: {}, 支付单号: {}", orderNo, amount, paymentNo);
        return paymentNo;
    }
    
    @Override
    public boolean queryPaymentStatus(String paymentNo) {
        // 模拟查询支付状态，默认返回成功
        log.info("查询支付状态 - 支付单号: {}, 状态: 已支付", paymentNo);
        return true;
    }
}
