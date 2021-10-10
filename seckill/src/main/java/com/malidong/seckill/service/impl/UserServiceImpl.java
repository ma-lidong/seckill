package com.malidong.seckill.service.impl;

import com.malidong.seckill.exception.GlobalException;
import com.malidong.seckill.mapper.UserMapper;
import com.malidong.seckill.pojo.User;
import com.malidong.seckill.service.UserService;
import com.malidong.seckill.utils.CookieUtil;
import com.malidong.seckill.utils.JsonUtil;
import com.malidong.seckill.utils.MD5Util;
import com.malidong.seckill.utils.UUIDUtil;
import com.malidong.seckill.vo.LoginVo;
import com.malidong.seckill.vo.RespBean;
import com.malidong.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        User user = userMapper.selectById(Long.parseLong(mobile));
        if(user == null){
            throw new GlobalException(RespBeanEnum.SESSION_ERROR);
        }
        if(!MD5Util.formPassToDBPass(password, user.getSalt()).equals(user.getPassword())){
            throw  new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        String ticket = UUIDUtil.uuid();
        // 存入redis
        redisTemplate.opsForValue().set("user:" + ticket, JsonUtil.object2JsonStr(user));
        // 存入cookie
        CookieUtil.setCookie(request, response, "userTicket", ticket);
        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicket)){
            return null;
        }
        String userJson = (String) redisTemplate.opsForValue().get("user:" + userTicket);
        User user = JsonUtil.jsonStr2Object(userJson, User.class);
        if(user != null){
            CookieUtil.setCookie(request, response, "userTicket", userTicket);
        }
        return user;
    }
}
