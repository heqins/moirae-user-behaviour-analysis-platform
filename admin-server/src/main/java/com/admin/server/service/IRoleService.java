package com.admin.server.service;

import com.api.common.bo.SysRole;
import com.api.common.param.admin.UserLoginParam;

public interface IRoleService {

    SysRole getRoleByName(String name);

}
