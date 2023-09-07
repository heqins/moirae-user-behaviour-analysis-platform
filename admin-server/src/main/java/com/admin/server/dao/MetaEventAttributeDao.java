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

}
