package com.admin.server.service;

import com.admin.server.model.bo.MetaEventAttribute;
import com.api.common.model.param.admin.UpdateMetaEventAttributeParam;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface IMetaEventAttributeService {

    List<MetaEventAttribute> queryByName(List<String> attributeNames, String appId);

    void batchInsertAttributes(List<MetaEventAttribute> metaEventAttributes);

    void updateMetaEventAttribute(UpdateMetaEventAttributeParam attributeParam);

    IPage<MetaEventAttribute> pageQueryByName(String appId, String eventName, String attributeName, Integer pageNum, Integer pageSize);
}
