package com.malidong.seckill.controller;

import com.malidong.seckill.pojo.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @RequestMapping("/toList")
    public String toList(User user){
        System.out.println(user);
        return "goodsList";
    }
}
