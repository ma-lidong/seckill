package com.malidong.seckill.controller;

import com.malidong.seckill.pojo.User;
import com.malidong.seckill.service.OrderService;
import com.malidong.seckill.vo.OrderDetailVo;
import com.malidong.seckill.vo.RespBean;
import com.malidong.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @RequestMapping("/detail")
    @ResponseBody
    public RespBean detail(User user, Long orderId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        OrderDetailVo detail = orderService.detail(orderId);
        return RespBean.success(detail);
    }
}
