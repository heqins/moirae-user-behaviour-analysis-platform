package com.admin.server.service.impl;

import com.admin.server.dao.AttributeDao;
import com.admin.server.service.IAttributeService;
import com.admin.server.service.IMetaEventAttributeService;
import com.api.common.bo.Attribute;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heqin
 */
@Service
@Slf4j
public class AttributeServiceImpl implements IAttributeService {


    @Resource
    private AttributeDao attributeDao;

    @Override
    public List<Attribute> queryByName(List<String> attributeNames, String appId) {
        return null;
    }
}
