package com.train.order.dto;

import lombok.Data;

/**
 * 创建订单DTO
 */
@Data
public class CreateOrderDTO {
    
    private Long userId;
    
    private Long ticketId;
    
    private Integer seatCount;
}
