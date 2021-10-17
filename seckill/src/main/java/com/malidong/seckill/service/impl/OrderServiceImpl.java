package com.malidong.seckill.service.impl;

import com.malidong.seckill.exception.GlobalException;
import com.malidong.seckill.mapper.OrderMapper;
import com.malidong.seckill.mapper.SeckillGoodsMapper;
import com.malidong.seckill.mapper.SeckillOrderMapper;
import com.malidong.seckill.pojo.Order;
import com.malidong.seckill.pojo.SeckillGoods;
import com.malidong.seckill.pojo.SeckillOrder;
import com.malidong.seckill.pojo.User;
import com.malidong.seckill.service.GoodsService;
import com.malidong.seckill.service.OrderService;
import com.malidong.seckill.utils.MD5Util;
import com.malidong.seckill.utils.UUIDUtil;
import com.malidong.seckill.vo.GoodsVo;
import com.malidong.seckill.vo.OrderDetailVo;
import com.malidong.seckill.vo.RespBeanEnum;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
public class OrderServiceImpl implements OrderService {
    @Resource
    private SeckillGoodsMapper seckillGoodsMapper;
    @Resource
    private OrderMapper orderMapper;
    @Resource
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @Transactional
    public Order seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SeckillGoods seckillGoods = seckillGoodsMapper.selectByGoodsId(goods.getId());

        boolean result = seckillGoodsMapper.updateGoodsId(goods.getId());
        if(result == false){
            valueOperations.set("isStockEmpty:"+ goods.getId(), "0");
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
        valueOperations.set("order:"+user.getId()+":"+goods.getId(),seckillOrder);
        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if(orderId == null){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVoByGoodsId = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detailVo = new OrderDetailVo();
        detailVo.setOrder(order);
        detailVo.setGoodsVo(goodsVoByGoodsId);
        return detailVo;
    }

    @Override
    public String createPath(User user, Long goodsId) {
        String str = MD5Util.md5(UUIDUtil.uuid() + "123456");
        redisTemplate.opsForValue().set("seckillPath:" + user.getId() + ":" + goodsId, str, 60, TimeUnit.SECONDS);
        return str;
    }

    @Override
    public boolean checkPath(User user, Long goodsId, String path) {
        if (user == null || goodsId < 0 || StringUtils.isEmpty(path)) {
            return false;
        }
        String redisPath = (String) redisTemplate.opsForValue().get("seckillPath:" + user.getId() + ":" + goodsId);
        return path.equals(redisPath);
    }

    @Override
    public boolean checkCaptcha(User user, Long goodsId, String captcha) {
        if (StringUtils.isEmpty(captcha) || user == null || goodsId < 0) {
            return false;
        }
        String redisCaptcha = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(redisCaptcha);
    }
}
