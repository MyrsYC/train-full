package com.train.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.train.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单Mapper
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
}
