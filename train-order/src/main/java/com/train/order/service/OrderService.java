package com.train.order.service;

import com.train.order.dto.CreateOrderDTO;
import com.train.order.vo.OrderVO;

import java.util.List;

/**
 * 订单服务接口
 */
public interface OrderService {
    
    /**
     * 创建订单
     */
    OrderVO createOrder(CreateOrderDTO createOrderDTO);
    
    /**
     * 支付订单
     */
    boolean payOrder(Long orderId);
    
    /**
     * 取消订单
     */
    boolean cancelOrder(Long orderId);
    
    /**
     * 查询用户订单
     */
    List<OrderVO> getUserOrders(Long userId);
    
    /**
     * 根据订单号查询订单
     */
    OrderVO getOrderByOrderNo(String orderNo);
}
