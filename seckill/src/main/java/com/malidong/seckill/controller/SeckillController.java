package com.malidong.seckill.controller;

import com.malidong.seckill.config.AccessLimit;
import com.malidong.seckill.exception.GlobalException;
import com.malidong.seckill.pojo.SeckillOrder;
import com.malidong.seckill.pojo.User;
import com.malidong.seckill.rabbitmq.MQSender;
import com.malidong.seckill.service.GoodsService;
import com.malidong.seckill.service.OrderService;
import com.malidong.seckill.service.SeckillOrderService;
import com.malidong.seckill.utils.JsonUtil;
import com.malidong.seckill.vo.GoodsVo;
import com.malidong.seckill.vo.RespBean;
import com.malidong.seckill.vo.RespBeanEnum;
import com.malidong.seckill.vo.SeckillMessage;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Controller
@RequestMapping("/seckill")
public class SeckillController implements InitializingBean {
    @Autowired
    private GoodsService goodsService;
    @Autowired
    private SeckillOrderService seckillOrderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;
    @Autowired
    private OrderService orderService;

    private Map<Long, Boolean> EmptyStockMap = new HashMap<>();

    @RequestMapping(value = "/{path}/doSeckill", method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSeckill(User user, Long goodsId, @PathVariable String path){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        ValueOperations valueOperations = redisTemplate.opsForValue();


        boolean check = orderService.checkPath(user, goodsId, path);
        if (!check) {
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }


        SeckillOrder seckillOrder = (SeckillOrder) valueOperations.get("order:" + user.getId() + ":" + goodsId);
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        if (EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        Long stock = valueOperations.decrement("seckillGoods:" + goodsId);
        if(stock < 0){
            EmptyStockMap.put(goodsId, true);
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SeckillMessage seckillMessage = new SeckillMessage(user, goodsId);
        mqSender.send(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
    }

    @RequestMapping(value = "/result", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user, goodsId);
        return RespBean.success(orderId);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(list)){
            return;
        }
        list.forEach(goodsVo -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goodsVo.getId(), goodsVo.getStockCount());
            EmptyStockMap.put(goodsVo.getId(), false);
        });
    }

    @AccessLimit(second = 5, maxCount = 5, needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha, HttpServletRequest request) {
        if (user == null) {
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }

        boolean check = orderService.checkCaptcha(user, goodsId, captcha);
        if (!check) {
            return RespBean.error(RespBeanEnum.ERROR_CAPTCHA);
        }

        String str = orderService.createPath(user, goodsId);
        return RespBean.success(str);
    }

    @RequestMapping(value = "/captcha", method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if (user == null || goodsId < 0) {
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //设置请求头为输出图片的类型
        response.setContentType("image/jpg");
        response.setHeader("Pargam", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);
        //生成验证码，将结果放入Redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 32, 3);
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 300,
                TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("验证码生成失败", e.getMessage());
        }
    }
}
