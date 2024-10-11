package com.atguigu.gulimall.product.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.gulimall.product.entity.PmsCommentReplayEntity;

import java.util.Map;

/**
 * ??Ʒ???ۻظ???ϵ
 *
 * @author thehou
 * @email thehou555@gmail.com
 * @date 2024-10-11 12:39:02
 */
public interface PmsCommentReplayService extends IService<PmsCommentReplayEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

