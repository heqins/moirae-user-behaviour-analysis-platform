package com.admin.server.service.impl;

import com.admin.server.dao.SysRoleDao;
import com.admin.server.service.IRoleService;
import com.admin.server.model.bo.SysRole;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

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
