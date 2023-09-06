package com.admin.server.dao;

import com.admin.server.mapper.SysUserMapper;
import com.api.common.bo.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class SysUserDao {

    @Resource
    private SysUserMapper sysUserMapper;

    public SysUser getUser(String username, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SysUser::getPassword, password);
        queryWrapper.eq(SysUser::getUsername, username);

        return sysUserMapper.selectOne(queryWrapper);
    }
}
