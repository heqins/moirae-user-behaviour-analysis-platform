package com.report.sink.service;

import com.api.common.dto.TableFieldDTO;

import java.util.List;
import java.util.Map;

/**
 * @author heqin
 */
public interface ICacheService {

    Map<String, String> getAppFieldsCache(String appId);

    List<String> getAppRulesCache(String appId);

    void setAppFieldCache(String appId, TableFieldDTO tableFieldDTO);

}
