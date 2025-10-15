package com.train.ticket.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.train.common.entity.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalTime;

/**
 * 列车实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("t_train")
public class Train extends BaseEntity {
    
    private String trainNumber;
    
    private String trainType;
    
    private String startStation;
    
    private String endStation;
    
    private LocalTime startTime;
    
    private LocalTime endTime;
    
    private Integer totalSeats;
    
    private Integer status;
}
