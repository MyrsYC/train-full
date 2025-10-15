package com.train.ticket.service;

import com.train.ticket.dto.TicketQueryDTO;
import com.train.ticket.vo.TicketVO;

import java.util.List;

/**
 * 车票服务接口
 */
public interface TicketService {
    
    /**
     * 查询车票
     */
    List<TicketVO> queryTickets(TicketQueryDTO queryDTO);
    
    /**
     * 根据ID查询车票
     */
    TicketVO getTicketById(Long id);
    
    /**
     * 锁定车票（购票时）
     */
    boolean lockTicket(Long ticketId, Integer count);
    
    /**
     * 释放车票（取消订单时）
     */
    boolean releaseTicket(Long ticketId, Integer count);
}
