package com.train.order.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.train.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_order")
public class Order extends BaseEntity {
    
    private String orderNo;
    
    private Long userId;
    
    private Long ticketId;
    
    private String trainNumber;
    
    private String startStation;
    
    private String endStation;
    
    private LocalDate travelDate;
    
    private String seatType;
    
    private Integer seatCount;
    
    private BigDecimal totalPrice;
    
    private Integer status;
    
    private LocalDateTime payTime;
    
    private LocalDateTime cancelTime;
}
