package com.atguigu.gulimall.member.dao;

import com.atguigu.gulimall.member.entity.MemberEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author thehou
 * @email thehou555@gmail.com
 * @date 2024-10-11 17:06:26
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
