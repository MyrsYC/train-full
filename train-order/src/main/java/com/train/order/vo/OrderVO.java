package com.train.order.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 订单响应VO
 */
@Data
public class OrderVO {
    
    private Long id;
    
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
    
    private String statusText;
    
    private LocalDateTime createTime;
    
    private LocalDateTime payTime;
}
