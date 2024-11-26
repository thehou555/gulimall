package com.atguigu.gulimall.product.service.impl;

import com.atguigu.common.constant.ProductConstant;
import com.atguigu.common.utils.PageUtils;
import com.atguigu.common.utils.Query;
import com.atguigu.gulimall.product.dao.AttrAttrgroupRelationDao;
import com.atguigu.gulimall.product.dao.AttrDao;
import com.atguigu.gulimall.product.dao.AttrGroupDao;
import com.atguigu.gulimall.product.dao.CategoryDao;
import com.atguigu.gulimall.product.entity.AttrAttrgroupRelationEntity;
import com.atguigu.gulimall.product.entity.AttrEntity;
import com.atguigu.gulimall.product.entity.AttrGroupEntity;
import com.atguigu.gulimall.product.entity.CategoryEntity;
import com.atguigu.gulimall.product.service.AttrService;
import com.atguigu.gulimall.product.vo.AttrGroupRelationVo;
import com.atguigu.gulimall.product.vo.AttrRespVo;
import com.atguigu.gulimall.product.vo.AttrVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("attrService")
public class AttrServiceImpl extends ServiceImpl<AttrDao, AttrEntity> implements AttrService {

    @Autowired
    private AttrAttrgroupRelationDao attrAttrgroupRelationDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private AttrGroupDao attrGroupDao;
    @Autowired
    private CategoryServiceImpl categoryService;
    @Autowired
    private AttrAttrgroupRelationServiceImpl attrAttrgroupRelationService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<AttrEntity> page = this.page(
                new Query<AttrEntity>().getPage(params),
                new QueryWrapper<AttrEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void saveAttr(AttrVo attr) {
        // 1. 保存基本数据
        this.save(attr);
        // 2. 保存关联数据
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() && attr.getAttrGroupId() != null) {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            relationEntity.setAttrGroupId(attr.getAttrGroupId());
            relationEntity.setAttrId(attr.getAttrId());
            attrAttrgroupRelationDao.insert(relationEntity);
        }
    }

    @Override
    public PageUtils queryBaseAttrPage(Map<String, Object> params, Long catelogId, String attrType) {
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<>();
        wrapper.eq("attr_type",
                "base".equalsIgnoreCase(attrType) ?
                        ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode() :
                        ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode());
        if (catelogId != 0) {
            wrapper.eq("catelog_id", catelogId);
        }
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and((obj) -> {
                obj.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params),
                wrapper);
        // 封装分页数据
        PageUtils pageUtils = new PageUtils(page);

        // 从分页数据中加入当前属性的“所属分类”和“所属分组”
        List<AttrEntity> records = page.getRecords();
        List<AttrRespVo> respVos = records.stream().map((attrEntity -> {
            AttrRespVo attrRespVo = new AttrRespVo();
            BeanUtils.copyProperties(attrEntity, attrRespVo);
            // 属性所属分类
            CategoryEntity categoryEntity = categoryDao.selectById(catelogId);
            if (categoryEntity != null) {
                attrRespVo.setCatelogName(categoryEntity.getName());
            }

            // 属性所属分组
            // 销售属性没有属性分组字段
            if ("base".equalsIgnoreCase(attrType)) {
                AttrAttrgroupRelationEntity attrAttrgroupRelationEntity = attrAttrgroupRelationDao.selectOne(
                        new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
                if (attrAttrgroupRelationEntity != null) {
                    AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrAttrgroupRelationEntity.getAttrGroupId());
                    if (attrGroupEntity != null) {
                        attrRespVo.setGroupName(attrGroupEntity.getAttrGroupName());
                    }
                }
            }
            return attrRespVo;
        })).collect(Collectors.toList());
        pageUtils.setList(respVos);
        return pageUtils;
    }

    @Override
    public AttrRespVo getAttrInfo(Long attrId) {
        // 1.获取销售基本属性
        AttrEntity attrEntity = this.getById(attrId);
        // 2.封装销售基本属性
        AttrRespVo attrRespVo = new AttrRespVo();
        BeanUtils.copyProperties(attrEntity, attrRespVo);
        // 3.设置属性所属分组
        AttrAttrgroupRelationEntity relationEntity = attrAttrgroupRelationDao.selectOne(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attrEntity.getAttrId()));
        if (relationEntity != null) {
            attrRespVo.setAttrGroupId(relationEntity.getAttrGroupId());
        }
        // 4.设置所属分类路径
        Long[] catelogPath = categoryService.findCatelogPath(attrEntity.getCatelogId());
        attrRespVo.setCatelogPath(catelogPath);
        return attrRespVo;
    }

    /**
     * 更新销售信息，同事更新关联关系
     * @param attr
     */
    @Transactional
    @Override
    public void updateAttr(AttrVo attr) {
        this.updateById(attr);
        if (attr.getAttrType() == ProductConstant.AttrEnum.ATTR_TYPE_SALE.getCode()
            || attr.getAttrId() == null) {
            return ;
        }
        AttrEntity attrEntity = new AttrEntity();
        BeanUtils.copyProperties(attr, attrEntity);
        AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
        relationEntity.setAttrId(attr.getAttrId());
        relationEntity.setAttrGroupId(attr.getAttrGroupId());
        int count = attrAttrgroupRelationDao.selectCount(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
        if (count > 0) {
            attrAttrgroupRelationDao.update(relationEntity,
                    new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_id", attr.getAttrId()));
        } else {
            attrAttrgroupRelationDao.insert(relationEntity);
        }

    }

    /**
     * 查询属性分组关联的所有属性
     * @param attrgroupId
     * @return
     */
    @Override
    public List<AttrEntity> getRelationAttr(Long attrgroupId) {
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().eq("attr_group_id", attrgroupId
                ));
        List<Long> attrIds = relationEntities.stream().map((entity) -> entity.getAttrId()).collect(Collectors.toList());
        return this.baseMapper.selectBatchIds(attrIds);
    }

    /**
     * 批量删除属性与分组的关联关系
     * @param vos
     */
    @Override
    public void deleteRelation(AttrGroupRelationVo[] vos) {
        List<AttrGroupRelationVo> relationVos = Arrays.asList(vos);
        List<AttrAttrgroupRelationEntity> entities = relationVos.stream().map((relationVo) -> {
            AttrAttrgroupRelationEntity relationEntity = new AttrAttrgroupRelationEntity();
            BeanUtils.copyProperties(relationVo, relationEntity);
            return relationEntity;
        }).collect(Collectors.toList());
        attrAttrgroupRelationDao.deleteBatchRelations(entities);
    }

    /**
     * 查询当前属性分组未关联的属性
     * @param params
     * @param attrgroupId
     * @return
     */
    @Override
    public PageUtils getNoRelationAttr(Map<String, Object> params, Long attrgroupId) {
        // 1、当前分组只能展示自己所属分类里面的所有属性
        // 先查询出当前分组所属的分类
        AttrGroupEntity attrGroupEntity = attrGroupDao.selectById(attrgroupId);
        Long catelogId = attrGroupEntity.getCatelogId();
        // 2、当前分组可以关联但未关联的属性
        // 2.1、当前分类下的所有分组
        List<AttrGroupEntity> attrGroupEntities = attrGroupDao.selectList(new QueryWrapper<AttrGroupEntity>().eq("catelog_id", catelogId));
        List<Long> attrGroupIds = attrGroupEntities.stream().map(AttrGroupEntity::getAttrGroupId).collect(Collectors.toList());
        // 2.2、查询分组下关联的属性
        List<AttrAttrgroupRelationEntity> relationEntities = attrAttrgroupRelationDao.selectList(
                new QueryWrapper<AttrAttrgroupRelationEntity>().in("attr_group_id", attrGroupIds));
        List<Long> attrIds = relationEntities.stream().map(AttrAttrgroupRelationEntity::getAttrId).collect(Collectors.toList());
        // 2.3、从当前分类的所有属性中移除已关联的属性
        QueryWrapper<AttrEntity> wrapper = new QueryWrapper<AttrEntity>().eq("catelog_id", catelogId)
                .eq("attr_type", ProductConstant.AttrEnum.ATTR_TYPE_BASE.getCode());
        String key = (String) params.get("key");
        if (StringUtils.isNotEmpty(key)) {
            wrapper.and(w -> {
                w.eq("attr_id", key).or().like("attr_name", key);
            });
        }
        IPage<AttrEntity> page = this.page(new Query<AttrEntity>().getPage(params), wrapper);
        return new PageUtils(page);
    }

}
