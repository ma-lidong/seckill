package com.malidong.seckill.rabbitmq;

import com.malidong.seckill.pojo.SeckillOrder;
import com.malidong.seckill.pojo.User;
import com.malidong.seckill.service.GoodsService;
import com.malidong.seckill.service.OrderService;
import com.malidong.seckill.utils.JsonUtil;
import com.malidong.seckill.vo.GoodsVo;
import com.malidong.seckill.vo.SeckillMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class MQReceiver {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private OrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void receive(String message){
        SeckillMessage seckillMessage = JsonUtil.jsonStr2Object(message, SeckillMessage.class);
        Long goodId = seckillMessage.getGoodId();
        User user = seckillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodId);
        if(goodsVo.getStockCount() < 1){
            return;
        }
        SeckillOrder seckillOrder = (SeckillOrder)redisTemplate.opsForValue().get("order:" + user.getId() + ":" + goodId);
        if(seckillOrder != null){
            return;
        }
        orderService.seckill(user, goodsVo);
    }
}
