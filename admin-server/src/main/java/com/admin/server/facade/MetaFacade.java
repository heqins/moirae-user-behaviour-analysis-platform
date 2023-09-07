package com.admin.server.facade;

import com.admin.server.service.IAttributeService;
import com.admin.server.service.IMetaEventAttributeService;
import com.admin.server.service.IMetaEventService;
import com.api.common.param.admin.CreateMetaEventAttributeParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
@Slf4j
public class MetaFacade {

    @Resource
    private IMetaEventService metaEventService;

    @Resource
    private IMetaEventAttributeService metaEventAttributeService;

    @Resource
    private IAttributeService attributeService;

    public void createMetaEventAttribute(CreateMetaEventAttributeParam param) {

    }


}
