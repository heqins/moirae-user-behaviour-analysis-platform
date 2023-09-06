package com.admin.server.dao;

import com.admin.server.mapper.MetaEventMapper;
import com.api.common.bo.MetaEvent;
import com.api.common.enums.MetaEventStatusEnum;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class MetaEventDao {

    @Resource
    private MetaEventMapper metaEventMapper;

    public IPage<MetaEvent> selectPageByAppId(String appId, Integer pageNum, Integer pageSize) {
        Page<MetaEvent> page = new Page<>(pageNum, pageSize);

        LambdaQueryWrapper<MetaEvent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MetaEvent::getAppId, appId);

        return metaEventMapper.selectPage(page, queryWrapper);
    }

    public void changeMetaEventStatus(String appId, String eventName, Integer status) {
        LambdaQueryWrapper<MetaEvent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MetaEvent::getAppId, appId);
        queryWrapper.eq(MetaEvent::getEventName, eventName);

        MetaEvent update = new MetaEvent();
        update.setStatus(status);

        metaEventMapper.update(update, queryWrapper);
    }
}
