package com.admin.server.service.impl;

import com.admin.server.dao.MetaAttributeRelationDao;
import com.admin.server.service.IMetaAttributeRelationService;
import com.api.common.bo.MetaAttributeRelation;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heqin
 */
@Service
@Slf4j
public class MetaAttributeRelationServiceImpl implements IMetaAttributeRelationService {

    @Resource
    private MetaAttributeRelationDao metaAttributeRelationDao;

    @Override
    public IPage<MetaAttributeRelation> queryMetaEventAttributes(String appId, String eventName, Integer pageNum, Integer pageSize) {
        return metaAttributeRelationDao.queryMetaEventAttributes(appId, eventName, pageNum, pageSize);
    }

    @Override
    public void batchInsertAttributes(List<MetaAttributeRelation> attributes) {
        metaAttributeRelationDao.batchInsertAttributes(attributes);
    }
}
