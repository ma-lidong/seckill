package com.malidong.seckill.mapper;

import com.malidong.seckill.pojo.SeckillOrder;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeckillOrderMapper {
    boolean insertAll(SeckillOrder seckillOrder);
    SeckillOrder selectByUserId(Long userId, Long goodsId);
}
