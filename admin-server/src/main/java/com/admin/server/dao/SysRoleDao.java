package com.admin.server.dao;

import com.admin.server.mapper.mysql.SysRoleMapper;
import com.admin.server.model.bo.SysRole;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SysRoleDao {

    @Resource
    private SysRoleMapper sysRoleMapper;

    public List<SysRole> getByName(String name) {
        LambdaQueryWrapper<SysRole> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysRole::getRoleName, name);

        return sysRoleMapper.selectList(queryWrapper);
    }
}
