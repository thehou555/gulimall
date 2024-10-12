package com.atguigu.gulimall.order.dao;

import com.atguigu.gulimall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 * 
 * @author thehou
 * @email thehou555@gmail.com
 * @date 2024-10-11 16:41:00
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {
	
}
