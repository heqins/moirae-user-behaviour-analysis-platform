package com.admin.server.service;

import com.api.common.bo.Attribute;

import java.util.List;

public interface IAttributeService {

    List<Attribute> queryByName(List<String> attributeNames, String appId);

}
