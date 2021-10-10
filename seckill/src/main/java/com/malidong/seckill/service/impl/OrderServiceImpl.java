package com.malidong.seckill.service.impl;

import com.malidong.seckill.mapper.OrderMapper;
import com.malidong.seckill.mapper.SeckillGoodsMapper;
import com.malidong.seckill.mapper.SeckillOrderMapper;
import com.malidong.seckill.pojo.Order;
import com.malidong.seckill.pojo.SeckillGoods;
import com.malidong.seckill.pojo.SeckillOrder;
import com.malidong.seckill.pojo.User;
import com.malidong.seckill.service.OrderService;
import com.malidong.seckill.vo.GoodsVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public Order seckill(User user, GoodsVo goods) {
        SeckillGoods seckillGoods = seckillGoodsMapper.selectByGoodsId(goods.getId());
        seckillGoods.setStockCount(seckillGoods.getStockCount() - 1);
        seckillGoodsMapper.updateGoodsId(seckillGoods);
        if(seckillGoods.getStockCount() < 1){
            return null;
        }
        //生成订单
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        order.setPayDate(new Date());
        orderMapper.insertAll(order);
        //生成秒杀订单
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderMapper.insertAll(seckillOrder);
        return order;
    }
}
