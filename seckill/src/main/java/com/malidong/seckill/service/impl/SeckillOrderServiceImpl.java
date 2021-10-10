package com.malidong.seckill.service.impl;

import com.malidong.seckill.mapper.SeckillOrderMapper;
import com.malidong.seckill.pojo.SeckillOrder;
import com.malidong.seckill.service.SeckillOrderService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public SeckillOrder selectByUserId(Long userId, Long goodsId) {
        return seckillOrderMapper.selectByUserId(userId, goodsId);
    }
}
