package com.report.sink.service;

import com.api.common.model.dto.admin.AppDTO;

public interface IAppService {

    AppDTO getAppInfo(String appId);

}
