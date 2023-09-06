package com.report.sink.service;

import com.api.common.dto.admin.AppDTO;

public interface IAppService {

    void generateApp(String appName, String describe);

    AppDTO getAppInfo(String appId);

}
