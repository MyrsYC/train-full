package com.train.ticket.controller;

import com.train.common.result.Result;
import com.train.ticket.dto.TicketQueryDTO;
import com.train.ticket.service.TicketService;
import com.train.ticket.vo.TicketVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 车票控制器
 */
@RestController
@RequestMapping("/ticket")
@RequiredArgsConstructor
public class TicketController {
    
    private final TicketService ticketService;
    
    /**
     * 查询车票
     */
    @PostMapping("/query")
    public Result<List<TicketVO>> queryTickets(@RequestBody TicketQueryDTO queryDTO) {
        List<TicketVO> tickets = ticketService.queryTickets(queryDTO);
        return Result.success(tickets);
    }
    
    /**
     * 根据ID查询车票
     */
    @GetMapping("/{id}")
    public Result<TicketVO> getTicketById(@PathVariable Long id) {
        TicketVO ticketVO = ticketService.getTicketById(id);
        return Result.success(ticketVO);
    }
}
