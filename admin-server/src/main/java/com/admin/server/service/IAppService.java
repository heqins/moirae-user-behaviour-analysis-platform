package com.admin.server.service;

import com.admin.server.model.bo.App;
import com.api.common.model.param.admin.CreateAppParam;
import com.api.common.model.vo.PageVo;
import com.api.common.model.vo.admin.AppPageVo;

public interface IAppService {

    void createApp(CreateAppParam createAppParam);

    PageVo<AppPageVo> getAvailableApps(Integer pageNum, Integer pageSize);

    App getByAppID(String appId);

}
