package com.admin.server.service;

import com.api.common.bo.MetaEvent;
import com.api.common.param.admin.CreateMetaEventParam;
import com.api.common.vo.PageVo;
import com.api.common.vo.admin.MetaEventsPageVo;

public interface IMetaEventService {

    PageVo<MetaEventsPageVo> queryMetaEventsByPage(Integer pageNum, Integer pageSize, String appId);

    void enableMetaEvent(String appId, String eventName);

    void disableMetaEvent(String appId, String eventName);

    void createMetaEvent(CreateMetaEventParam createMetaEventParam);

    MetaEvent selectByAppId(String appId, String eventName);

}
