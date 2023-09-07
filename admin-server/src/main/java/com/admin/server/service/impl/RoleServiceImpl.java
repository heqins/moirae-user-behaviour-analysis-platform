package com.admin.server.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.crypto.digest.Digester;
import cn.hutool.crypto.digest.MD5;
import com.admin.server.dao.SysRoleDao;
import com.admin.server.dao.SysUserDao;
import com.admin.server.service.IRoleService;
import com.admin.server.service.IUserService;
import com.api.common.bo.SysRole;
import com.api.common.bo.SysUser;
import com.api.common.enums.ResponseStatusEnum;
import com.api.common.error.ResponseException;
import com.api.common.param.admin.UserLoginParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

/**
 * @author heqin
 */
@Service
@Slf4j
public class RoleServiceImpl implements IRoleService {

    @Resource
    private SysRoleDao sysRoleDao;

    @Override
    public SysRole getRoleByName(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }

        List<SysRole> roleList = sysRoleDao.getByName(name);
        if (CollectionUtils.isEmpty(roleList)) {
            return null;
        }

        return roleList.get(0);
    }
}
