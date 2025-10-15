package com.train.order.controller;

import com.train.common.result.Result;
import com.train.order.dto.CreateOrderDTO;
import com.train.order.service.OrderService;
import com.train.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {
    
    private final OrderService orderService;
    
    /**
     * 创建订单
     */
    @PostMapping("/create")
    public Result<OrderVO> createOrder(@RequestBody CreateOrderDTO createOrderDTO) {
        OrderVO orderVO = orderService.createOrder(createOrderDTO);
        return Result.success(orderVO);
    }
    
    /**
     * 支付订单
     */
    @PostMapping("/pay/{orderId}")
    public Result<Boolean> payOrder(@PathVariable Long orderId) {
        boolean success = orderService.payOrder(orderId);
        return Result.success(success);
    }
    
    /**
     * 取消订单
     */
    @PostMapping("/cancel/{orderId}")
    public Result<Boolean> cancelOrder(@PathVariable Long orderId) {
        boolean success = orderService.cancelOrder(orderId);
        return Result.success(success);
    }
    
    /**
     * 查询用户订单
     */
    @GetMapping("/user/{userId}")
    public Result<List<OrderVO>> getUserOrders(@PathVariable Long userId) {
        List<OrderVO> orders = orderService.getUserOrders(userId);
        return Result.success(orders);
    }
    
    /**
     * 根据订单号查询订单
     */
    @GetMapping("/no/{orderNo}")
    public Result<OrderVO> getOrderByOrderNo(@PathVariable String orderNo) {
        OrderVO orderVO = orderService.getOrderByOrderNo(orderNo);
        return Result.success(orderVO);
    }
}
