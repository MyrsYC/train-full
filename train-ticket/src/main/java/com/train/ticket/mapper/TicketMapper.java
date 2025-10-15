package com.train.ticket.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.train.ticket.entity.Ticket;
import org.apache.ibatis.annotations.Mapper;

/**
 * 车票Mapper
 */
@Mapper
public interface TicketMapper extends BaseMapper<Ticket> {
}
