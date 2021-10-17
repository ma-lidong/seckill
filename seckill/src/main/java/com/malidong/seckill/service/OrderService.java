package com.malidong.seckill.service;

import com.malidong.seckill.pojo.Order;
import com.malidong.seckill.pojo.User;
import com.malidong.seckill.vo.GoodsVo;
import com.malidong.seckill.vo.OrderDetailVo;

public interface OrderService {
    Order seckill(User user, GoodsVo goods);
    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);
    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
