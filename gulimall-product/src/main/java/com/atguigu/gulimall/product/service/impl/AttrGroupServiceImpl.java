package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.service.AttrGroupService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("attrGroupService")
public class AttrGroupServiceImpl extends ServiceImpl<AttrGroupDao, AttrGroupEntity> implements AttrGroupService {


    @Override
    public PageUtils queryPage(Map<String, Object> params, Long catelogId) {
        String key = (String) params.get("key");
        LambdaQueryWrapper<AttrGroupEntity> wrapper = new LambdaQueryWrapper<>();
        // 如果key不为空，就根据key查询
        // select * from pms_attr_group where (attr_group_id = key or attr_group_name like %key%) and catelog_id = catelogId
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(
                    obj -> obj.eq(AttrGroupEntity::getAttrGroupId, key).or().like(AttrGroupEntity::getAttrGroupName, key)
            );
        }
        if (catelogId != 0) {
            wrapper.eq(AttrGroupEntity::getCatelogId, catelogId);
        }
        IPage<AttrGroupEntity> page = this.page(
                new Query<AttrGroupEntity>().getPage(params),
                wrapper
        );

        return new PageUtils(page);
    }

}
