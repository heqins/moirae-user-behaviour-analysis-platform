package com.admin.server.dao;

import com.admin.server.mapper.MetaAttributeRelationMapper;
import com.api.common.bo.MetaAttributeRelation;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class MetaAttributeRelationDao extends ServiceImpl<MetaAttributeRelationMapper, MetaAttributeRelation> {

    @Resource
    private MetaAttributeRelationMapper metaAttributeRelationMapper;

    public IPage<MetaAttributeRelation> queryMetaEventAttributes(String appId, String eventName, Integer pageNum, Integer pageSize) {
        Page<MetaAttributeRelation> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MetaAttributeRelation> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaAttributeRelation::getAppId, appId);
        wrapper.eq(MetaAttributeRelation::getEventName, eventName);

        return metaAttributeRelationMapper.selectPage(page, wrapper);
    }

    public void batchInsertAttributes(List<MetaAttributeRelation> attributes) {
        metaAttributeRelationMapper.batchInsertAttributes(attributes);
    }
}
