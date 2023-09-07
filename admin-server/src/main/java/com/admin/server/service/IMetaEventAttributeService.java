package com.admin.server.service;

import com.api.common.bo.MetaEventAttribute;
import com.api.common.param.admin.CreateMetaEventParam;
import com.api.common.vo.admin.MetaEventsPageVo;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

public interface IMetaEventAttributeService {

    IPage<MetaEventAttribute> queryMetaEventAttributes(String appId, String eventName, Integer pageNum, Integer pageSize);

}
