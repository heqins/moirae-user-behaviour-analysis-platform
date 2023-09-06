package com.admin.server.service;

import com.api.common.param.admin.UserLoginParam;

public interface IUserService {

    void doLogin(UserLoginParam userLoginParam);

}
