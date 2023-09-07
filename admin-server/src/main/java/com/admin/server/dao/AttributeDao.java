package com.admin.server.dao;

import com.admin.server.mapper.AttributeMapper;
import com.admin.server.mapper.MetaEventAttributeMapper;
import com.api.common.bo.Attribute;
import com.api.common.bo.MetaEventAttribute;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AttributeDao extends ServiceImpl<AttributeMapper, Attribute> {

    @Resource
    private AttributeMapper attributeMapper;

}
