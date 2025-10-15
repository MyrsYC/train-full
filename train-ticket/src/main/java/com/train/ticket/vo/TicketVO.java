package com.train.ticket.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 车票响应VO
 */
@Data
public class TicketVO {
    
    private Long id;
    
    private Long trainId;
    
    private String trainNumber;
    
    private String startStation;
    
    private String endStation;
    
    private LocalDate travelDate;
    
    private String seatType;
    
    private BigDecimal price;
    
    private Integer remainSeats;
    
    private Integer totalSeats;
}
