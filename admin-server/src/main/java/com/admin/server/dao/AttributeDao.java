package com.admin.server.dao;

import com.admin.server.mapper.AttributeMapper;
import com.api.common.bo.Attribute;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class AttributeDao extends ServiceImpl<AttributeMapper, Attribute> {

    @Resource
    private AttributeMapper attributeMapper;

    public List<Attribute> selectByName(String appId, List<String> attributeNames) {
        LambdaQueryWrapper<Attribute> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Attribute::getAttributeName, attributeNames);
        queryWrapper.eq(Attribute::getAppId, appId);
        queryWrapper.eq(Attribute::getStatus, 1);

        return attributeMapper.selectList(queryWrapper);
    }

    public void batchInsertAttributes(List<Attribute> attributes) {
        attributeMapper.batchInsertAttributes(attributes);
    }
}
