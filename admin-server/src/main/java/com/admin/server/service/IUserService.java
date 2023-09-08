package com.admin.server.service;

import com.api.common.model.param.admin.UserLoginParam;

public interface IUserService {

    void doLogin(UserLoginParam userLoginParam);

    void doLogout();

}
