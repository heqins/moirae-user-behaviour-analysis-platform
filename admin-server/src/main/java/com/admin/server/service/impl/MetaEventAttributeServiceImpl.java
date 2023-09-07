package com.admin.server.service.impl;

import com.admin.server.dao.AttributeDao;
import com.admin.server.service.IMetaEventAttributeService;
import com.api.common.bo.MetaEventAttribute;
import com.api.common.param.admin.UpdateMetaEventAttributeParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author heqin
 */
@Service
@Slf4j
public class MetaEventAttributeServiceImpl implements IMetaEventAttributeService {

    @Resource
    private AttributeDao attributeDao;

    @Override
    public List<MetaEventAttribute> queryByName(List<String> attributeNames, String appId) {
        if (StringUtils.isEmpty(appId) || CollectionUtils.isEmpty(attributeNames)) {
            return Collections.emptyList();
        }

        return attributeDao.selectByName(appId, attributeNames);
    }

    @Override
    public void batchInsertAttributes(List<MetaEventAttribute> metaEventAttributes) {
        attributeDao.batchInsertAttributes(metaEventAttributes);
    }

    @Override
    public void updateMetaEventAttribute(UpdateMetaEventAttributeParam attributeParam) {

    }
}
