package com.train.ticket.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.train.common.exception.BusinessException;
import com.train.common.result.ResultCode;
import com.train.ticket.dto.TicketQueryDTO;
import com.train.ticket.entity.Ticket;
import com.train.ticket.mapper.TicketMapper;
import com.train.ticket.service.TicketService;
import com.train.ticket.vo.TicketVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 车票服务实现
 */
@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {
    
    private final TicketMapper ticketMapper;
    
    @Override
    public List<TicketVO> queryTickets(TicketQueryDTO queryDTO) {
        LambdaQueryWrapper<Ticket> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Ticket::getStartStation, queryDTO.getStartStation())
               .eq(Ticket::getEndStation, queryDTO.getEndStation())
               .eq(Ticket::getTravelDate, queryDTO.getTravelDate())
               .eq(Ticket::getStatus, 1);
        
        if (queryDTO.getSeatType() != null) {
            wrapper.eq(Ticket::getSeatType, queryDTO.getSeatType());
        }
        
        List<Ticket> tickets = ticketMapper.selectList(wrapper);
        
        return tickets.stream()
                .map(ticket -> {
                    TicketVO ticketVO = new TicketVO();
                    BeanUtil.copyProperties(ticket, ticketVO);
                    return ticketVO;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public TicketVO getTicketById(Long id) {
        Ticket ticket = ticketMapper.selectById(id);
        
        if (ticket == null) {
            throw new BusinessException(ResultCode.TICKET_NOT_AVAILABLE);
        }
        
        TicketVO ticketVO = new TicketVO();
        BeanUtil.copyProperties(ticket, ticketVO);
        return ticketVO;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean lockTicket(Long ticketId, Integer count) {
        Ticket ticket = ticketMapper.selectById(ticketId);
        
        if (ticket == null) {
            throw new BusinessException(ResultCode.TICKET_NOT_AVAILABLE);
        }
        
        if (ticket.getRemainSeats() < count) {
            throw new BusinessException(ResultCode.TICKET_NOT_AVAILABLE);
        }
        
        ticket.setRemainSeats(ticket.getRemainSeats() - count);
        return ticketMapper.updateById(ticket) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean releaseTicket(Long ticketId, Integer count) {
        Ticket ticket = ticketMapper.selectById(ticketId);
        
        if (ticket == null) {
            throw new BusinessException(ResultCode.TICKET_NOT_AVAILABLE);
        }
        
        ticket.setRemainSeats(ticket.getRemainSeats() + count);
        return ticketMapper.updateById(ticket) > 0;
    }
}
