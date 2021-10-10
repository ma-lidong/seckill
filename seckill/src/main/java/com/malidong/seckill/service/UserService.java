package com.malidong.seckill.service;

import com.malidong.seckill.pojo.User;
import com.malidong.seckill.vo.LoginVo;
import com.malidong.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public interface UserService {
    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    User getUserByCookie(String userTicket, HttpServletRequest request, HttpServletResponse response);
}
