package com.malidong.seckill.service;

import com.malidong.seckill.pojo.SeckillOrder;
import com.malidong.seckill.pojo.User;

public interface SeckillOrderService {
    SeckillOrder selectByUserId(Long userId, Long goodsId);
    Long getResult(User user, Long goodsId);
}
