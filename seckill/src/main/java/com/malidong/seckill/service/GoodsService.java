package com.malidong.seckill.service;

import com.malidong.seckill.vo.GoodsVo;

import java.util.List;

public interface GoodsService {
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
