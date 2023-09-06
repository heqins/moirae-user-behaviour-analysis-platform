package com.admin.server.service.impl;

import com.admin.server.dao.AppDao;
import com.admin.server.service.IAppService;
import com.admin.server.service.IUserService;
import com.admin.server.util.KeyUtil;
import com.api.common.bo.App;
import com.api.common.param.admin.CreateAppParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Resource
    private AppDao appDao;

}
