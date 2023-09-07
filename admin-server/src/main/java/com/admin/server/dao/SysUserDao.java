package com.admin.server.dao;

import com.admin.server.mapper.SysUserMapper;
import com.api.common.bo.SysUser;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

@Component
public class SysUserDao extends ServiceImpl<SysUserMapper, SysUser> {

    @Resource
    private SysUserMapper sysUserMapper;

    public SysUser getUser(String username, String password) {
        LambdaQueryWrapper<SysUser> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(SysUser::getPassword, password);
        queryWrapper.eq(SysUser::getUsername, username);

        return sysUserMapper.selectOne(queryWrapper);
    }

    public void insertUsers(List<SysUser> sysUserList) {
        if (CollectionUtils.isEmpty(sysUserList)) {
            return;
        }

        List<List<SysUser>> partition = Lists.partition(sysUserList, 50);
        for (List<SysUser> partitionList : partition) {
            saveBatch(partitionList);
        }
    }
}
