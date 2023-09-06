package com.admin.server.service;

import com.api.common.vo.MetaEventsPageVo;

public interface IMetaEventService {

    MetaEventsPageVo queryMetaEventsByPage(Integer pageNum, Integer pageSize, String appId);

    void enableMetaEvent(String appId, String eventName);

    void disableMetaEvent(String appId, String eventName);

}
