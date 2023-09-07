package com.report.sink.service;

import com.api.common.bo.MetaEvent;

/**
 * @author heqin
 */
public interface IMetaEventService {

    MetaEvent getMetaEvent(String appId, String eventName);

}
