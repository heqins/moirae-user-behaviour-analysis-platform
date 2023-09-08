package com.admin.server;

import com.admin.server.dao.SysUserDao;
import com.admin.server.service.IRoleService;
import com.admin.server.service.impl.UserServiceImpl;
import com.admin.server.util.KeyUtil;
import com.admin.server.model.bo.SysRole;
import com.admin.server.model.bo.SysUser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserTest {

    @Resource
    private UserServiceImpl userService;

    @Resource
    private SysUserDao sysUserDao;

    @Resource
    private IRoleService roleService;

    @Test
    public void testGenerateKey() {
        System.out.println(KeyUtil.generateAppId().toString());
    }

    @Test
    public void createUser() {
        String user = "testUser";
        String password = "qwerty";

        SysRole role = roleService.getRoleByName("超管");
        assert role != null;

        String s = userService.encryptMd5(password);

        SysUser sysUser = new SysUser();
        sysUser.setUsername(user);
        sysUser.setPassword(s);
        sysUser.setRoleId(role.getId());

        sysUserDao.insertUsers(List.of(sysUser));
    }
}
