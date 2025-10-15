package com.train.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.train.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 车票实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_ticket")
public class Ticket extends BaseEntity {
    
    private Long trainId;
    
    private String trainNumber;
    
    private String startStation;
    
    private String endStation;
    
    private LocalDate travelDate;
    
    private String seatType;
    
    private BigDecimal price;
    
    private Integer remainSeats;
    
    private Integer totalSeats;
    
    private Integer status;
}
