package com.malidong.seckill.mapper;

import com.malidong.seckill.pojo.Order;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface OrderMapper {
    boolean insertAll(Order order);
    Order selectById(Long orderId);
}
