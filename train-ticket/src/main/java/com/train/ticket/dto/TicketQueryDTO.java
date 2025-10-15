package com.train.ticket.dto;

import lombok.Data;

import java.time.LocalDate;

/**
 * 车票查询DTO
 */
@Data
public class TicketQueryDTO {
    
    private String startStation;
    
    private String endStation;
    
    private LocalDate travelDate;
    
    private String seatType;
}
