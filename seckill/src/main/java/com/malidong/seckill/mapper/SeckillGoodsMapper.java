package com.malidong.seckill.mapper;

import com.malidong.seckill.pojo.SeckillGoods;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SeckillGoodsMapper {
    SeckillGoods selectByGoodsId(Long goodsId);
    boolean updateGoodsId(Long goodsId);
}
