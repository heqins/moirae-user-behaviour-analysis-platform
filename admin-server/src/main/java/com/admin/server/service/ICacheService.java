package com.admin.server.service;

public interface ICacheService {

    void setMetaEventCache(String appId, String eventName, Integer status);

    void removeMetaEventCache(String appId, String eventName);
}
