package com.admin.server.service;

import com.api.common.bo.MetaEventAttribute;
import com.api.common.param.admin.UpdateMetaEventAttributeParam;

import java.util.List;

public interface IMetaEventAttributeService {

    List<MetaEventAttribute> queryByName(List<String> attributeNames, String appId);

    void batchInsertAttributes(List<MetaEventAttribute> metaEventAttributes);

    void updateMetaEventAttribute(UpdateMetaEventAttributeParam attributeParam);

}
