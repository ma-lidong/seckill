package com.malidong.seckill.controller;

import com.malidong.seckill.pojo.Order;
import com.malidong.seckill.pojo.SeckillOrder;
import com.malidong.seckill.pojo.User;
import com.malidong.seckill.service.GoodsService;
import com.malidong.seckill.service.OrderService;
import com.malidong.seckill.service.SeckillOrderService;
import com.malidong.seckill.vo.GoodsVo;
import com.malidong.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seckill")
public class SeckillController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private SeckillOrderService seckillOrderService;

    @RequestMapping("/doSeckill")
    public String doSeckill(Model model, User user, Long goodsId){
        if(user == null){
            return "login";
        }
        model.addAttribute("user", user);
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goods.getStockCount() < 1){
            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
            return "secKillFail";
        }
        // 重复购买
        SeckillOrder seckillOrder = seckillOrderService.selectByUserId(user.getId(), goodsId);
        if (seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }

        Order order = orderService.seckill(user, goods);
        model.addAttribute("order", order);
        model.addAttribute("goods", goods);
        return "orderDetail";
    }
}
