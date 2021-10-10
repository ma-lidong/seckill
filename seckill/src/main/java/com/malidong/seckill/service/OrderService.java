package com.malidong.seckill.service;

import com.malidong.seckill.pojo.Order;
import com.malidong.seckill.pojo.User;
import com.malidong.seckill.vo.GoodsVo;

public interface OrderService {
    Order seckill(User user, GoodsVo goods);
}
