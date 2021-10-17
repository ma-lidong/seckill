package com.malidong.seckill.controller;

import com.malidong.seckill.pojo.User;
import com.malidong.seckill.service.GoodsService;
import com.malidong.seckill.vo.DetailVo;
import com.malidong.seckill.vo.GoodsVo;
import com.malidong.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(User user, Model model, HttpServletRequest request, HttpServletResponse response){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String)valueOperations.get("goodsList");
        if(!StringUtils.isEmpty(html)){
            return html;
        }
        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findGoodsVo());
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        if(!StringUtils.isEmpty(html)){
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    @RequestMapping("/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail(User user, @PathVariable Long goodsId){
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        int secKillStatus = 0;
        int remainSeconds = 0;
        if(nowDate.before(startDate)){
            secKillStatus = 0;
            remainSeconds = (int)((startDate.getTime()-nowDate.getTime())/1000);
        }else if(nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else{
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setUser(user);
        detailVo.setGoodsVo(goodsVo);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setSecKillStatus(secKillStatus);

        return RespBean.success(detailVo);
    }
}
