package com.admin.server.service;

import com.api.common.bo.MetaAttributeRelation;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface IMetaAttributeRelationService {

    IPage<MetaAttributeRelation> queryMetaEventAttributes(String appId, String eventName, Integer pageNum, Integer pageSize);

    void batchInsertAttributes(List<MetaAttributeRelation> attributes);

}
