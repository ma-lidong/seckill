package com.malidong.seckill.service;

import com.malidong.seckill.pojo.SeckillOrder;

public interface SeckillOrderService {
    SeckillOrder selectByUserId(Long userId, Long goodsId);
}
