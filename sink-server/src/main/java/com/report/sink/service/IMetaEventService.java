package com.report.sink.service;


import com.report.sink.model.bo.MetaEvent;

/**
 * @author heqin
 */
public interface IMetaEventService {

    MetaEvent getMetaEvent(String appId, String eventName);

}
