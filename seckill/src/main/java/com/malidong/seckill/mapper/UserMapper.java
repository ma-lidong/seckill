package com.malidong.seckill.mapper;

import com.malidong.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper {
    User selectById(Long id);
}
