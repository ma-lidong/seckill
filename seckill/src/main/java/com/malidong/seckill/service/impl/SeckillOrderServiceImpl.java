package com.malidong.seckill.service.impl;

import com.malidong.seckill.mapper.SeckillOrderMapper;
import com.malidong.seckill.pojo.SeckillOrder;
import com.malidong.seckill.pojo.User;
import com.malidong.seckill.service.SeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Resource
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public SeckillOrder selectByUserId(Long userId, Long goodsId) {
        return seckillOrderMapper.selectByUserId(userId, goodsId);
    }

    @Override
    public Long getResult(User user, Long goodsId) {
        SeckillOrder seckillOrder = seckillOrderMapper.selectByUserId(user.getId(), goodsId);
        if (null != seckillOrder) {
            return seckillOrder.getOrderId();
        } else if (redisTemplate.hasKey("isStockEmpty:" + goodsId)) {
            return -1L;
        } else {
            return 0L;
        }
    }
}
