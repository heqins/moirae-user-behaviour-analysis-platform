package com.admin.server.dao;

import com.admin.server.mapper.MetaEventAttributeMapper;
import com.admin.server.mapper.MetaEventMapper;
import com.api.common.bo.MetaEvent;
import com.api.common.bo.MetaEventAttribute;
import com.api.common.enums.MetaEventStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class MetaEventAttributeDao extends ServiceImpl<MetaEventAttributeMapper, MetaEventAttribute> {

    @Resource
    private MetaEventAttributeMapper metaEventAttributeMapper;

    public IPage<MetaEventAttribute> queryMetaEventAttributes(String appId, String eventName, Integer pageNum, Integer pageSize) {
        Page<MetaEventAttribute> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<MetaEventAttribute> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaEventAttribute::getAppId, appId);
        wrapper.eq(MetaEventAttribute::getEventName, eventName);

        return metaEventAttributeMapper.selectPage(page, wrapper);
    }
}
