package com.train.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 返回码枚举
 */
@Getter
@AllArgsConstructor
public enum ResultCode {
    
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    
    // 用户相关 1xxx
    USER_NOT_FOUND(1001, "用户不存在"),
    USER_ALREADY_EXISTS(1002, "用户已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_INVALID(1004, "Token无效"),
    TOKEN_EXPIRED(1005, "Token已过期"),
    UNAUTHORIZED(1006, "未授权访问"),
    
    // 车票相关 2xxx
    TRAIN_NOT_FOUND(2001, "列车不存在"),
    TICKET_NOT_AVAILABLE(2002, "车票余票不足"),
    SEAT_NOT_AVAILABLE(2003, "座位不可用"),
    TICKET_SOLD_OUT(2004, "车票已售罄"),
    
    // 订单相关 3xxx
    ORDER_NOT_FOUND(3001, "订单不存在"),
    ORDER_ALREADY_PAID(3002, "订单已支付"),
    ORDER_EXPIRED(3003, "订单已过期"),
    ORDER_CANCELLED(3004, "订单已取消"),
    
    // 支付相关 4xxx
    PAYMENT_FAILED(4001, "支付失败"),
    REFUND_FAILED(4002, "退款失败"),
    
    // 系统相关 5xxx
    SYSTEM_ERROR(5001, "系统错误"),
    PARAM_ERROR(5002, "参数错误"),
    DATABASE_ERROR(5003, "数据库错误");

    private final Integer code;
    private final String message;
}
