package com.train.order.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.train.common.exception.BusinessException;
import com.train.common.result.ResultCode;
import com.train.order.dto.CreateOrderDTO;
import com.train.order.entity.Order;
import com.train.order.mapper.OrderMapper;
import com.train.order.service.OrderService;
import com.train.order.vo.OrderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 订单服务实现
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    
    private final OrderMapper orderMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public OrderVO createOrder(CreateOrderDTO createOrderDTO) {
        // 生成订单号
        String orderNo = "ORD" + IdUtil.getSnowflakeNextIdStr();
        
        // 创建订单
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(createOrderDTO.getUserId());
        order.setTicketId(createOrderDTO.getTicketId());
        order.setSeatCount(createOrderDTO.getSeatCount());
        order.setTotalPrice(BigDecimal.valueOf(100.00 * createOrderDTO.getSeatCount())); // 简化实现
        order.setStatus(0); // 0-待支付
        order.setDelFlag(0);
        
        orderMapper.insert(order);
        
        // 返回订单信息
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order, orderVO);
        orderVO.setStatusText("待支付");
        return orderVO;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean payOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        
        if (order.getStatus() != 0) {
            throw new BusinessException(ResultCode.ORDER_ALREADY_PAID);
        }
        
        order.setStatus(1); // 1-已支付
        order.setPayTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        
        if (order.getStatus() == 2) {
            throw new BusinessException(ResultCode.ORDER_CANCELLED);
        }
        
        order.setStatus(2); // 2-已取消
        order.setCancelTime(LocalDateTime.now());
        
        return orderMapper.updateById(order) > 0;
    }
    
    @Override
    public List<OrderVO> getUserOrders(Long userId) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getUserId, userId)
               .orderByDesc(Order::getCreateTime);
        
        List<Order> orders = orderMapper.selectList(wrapper);
        
        return orders.stream()
                .map(order -> {
                    OrderVO orderVO = new OrderVO();
                    BeanUtil.copyProperties(order, orderVO);
                    orderVO.setStatusText(getStatusText(order.getStatus()));
                    return orderVO;
                })
                .collect(Collectors.toList());
    }
    
    @Override
    public OrderVO getOrderByOrderNo(String orderNo) {
        LambdaQueryWrapper<Order> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Order::getOrderNo, orderNo);
        Order order = orderMapper.selectOne(wrapper);
        
        if (order == null) {
            throw new BusinessException(ResultCode.ORDER_NOT_FOUND);
        }
        
        OrderVO orderVO = new OrderVO();
        BeanUtil.copyProperties(order, orderVO);
        orderVO.setStatusText(getStatusText(order.getStatus()));
        return orderVO;
    }
    
    private String getStatusText(Integer status) {
        switch (status) {
            case 0: return "待支付";
            case 1: return "已支付";
            case 2: return "已取消";
            case 3: return "已完成";
            default: return "未知状态";
        }
    }
}
