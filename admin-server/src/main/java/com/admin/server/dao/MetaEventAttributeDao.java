package com.admin.server.dao;

import com.admin.server.mapper.MetaEventAttributeMapper;
import com.api.common.bo.MetaEventAttribute;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class MetaEventAttributeDao extends ServiceImpl<MetaEventAttributeMapper, MetaEventAttribute> {

    @Resource
    private MetaEventAttributeMapper metaEventAttributeMapper;

    public List<MetaEventAttribute> selectByName(String appId, List<String> attributeNames) {
        LambdaQueryWrapper<MetaEventAttribute> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(MetaEventAttribute::getAttributeName, attributeNames);
        queryWrapper.eq(MetaEventAttribute::getAppId, appId);
        queryWrapper.eq(MetaEventAttribute::getStatus, 1);

        return metaEventAttributeMapper.selectList(queryWrapper);
    }

    public MetaEventAttribute selectByEventAndAttributeName(String appId, String eventName, String attributeName) {
        LambdaQueryWrapper<MetaEventAttribute> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MetaEventAttribute::getAttributeName, attributeName);
        queryWrapper.eq(MetaEventAttribute::getAppId, appId);
        queryWrapper.eq(MetaEventAttribute::getStatus, 1);

        return metaEventAttributeMapper.selectOne(queryWrapper);
    }

    public void batchInsertAttributes(List<MetaEventAttribute> metaEventAttributes) {
        metaEventAttributeMapper.batchInsertAttributes(metaEventAttributes);
    }
}
