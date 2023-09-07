package com.admin.server.service.impl;

import com.admin.server.dao.MetaEventAttributeDao;
import com.admin.server.service.IMetaEventAttributeService;
import com.api.common.bo.MetaEventAttribute;
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
public class MetaEventAttributeServiceImpl implements IMetaEventAttributeService {

    @Resource
    private MetaEventAttributeDao metaEventAttributeDao;

    @Override
    public IPage<MetaEventAttribute> queryMetaEventAttributes(String appId, String eventName, Integer pageNum, Integer pageSize) {
        return metaEventAttributeDao.queryMetaEventAttributes(appId, eventName, pageNum, pageSize);
    }
}
