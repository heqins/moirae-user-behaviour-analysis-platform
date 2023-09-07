package com.admin.server.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.MD5;
import com.admin.server.dao.SysUserDao;
import com.admin.server.service.IUserService;
import com.api.common.bo.SysUser;
import com.api.common.enums.ResponseStatusEnum;
import com.api.common.error.ResponseException;
import com.api.common.param.admin.UserLoginParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/**
 * @author heqin
 */
@Service
@Slf4j
public class UserServiceImpl implements IUserService {

    @Value("${custom.security.salt}")
    private String saltStr;

    @Resource
    private SysUserDao sysUserDao;

    private Digester md5Digester;

    @PostConstruct
    public void init() {
        md5Digester = MD5.create().setSalt(saltStr.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void doLogin(UserLoginParam userLoginParam) {
        if (userLoginParam == null) {
            throw new ResponseException(ResponseStatusEnum.UNAUTHORIZED);
        }

        if (StpUtil.isLogin()) {
            return;
        }

        String passwordSalt = md5Digester.digestHex(userLoginParam.getPassword());
        SysUser user = sysUserDao.getUser(userLoginParam.getUsername(), passwordSalt);
        if (Objects.isNull(user)) {
            throw new ResponseException(ResponseStatusEnum.FORBIDDEN);
        }

        if (!Objects.equals(passwordSalt, user.getPassword())) {
            throw new ResponseException(ResponseStatusEnum.UNAUTHORIZED);
        }

        // 登录成功
        StpUtil.login(userLoginParam.getUsername());
    }

    @Override
    public void doLogout() {
        if (!StpUtil.isLogin()) {
            return;
        }

        StpUtil.logout();
    }

    public String encryptMd5(String text) {
        return md5Digester.digestHex(text);
    }
}
