package com.report.sink.dao;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.report.sink.mapper.mysql.MetaEventMapper;
import com.report.sink.model.bo.MetaEvent;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heqin
 */
@Component
public class MetaEventDao extends ServiceImpl<MetaEventMapper, MetaEvent> {

    @Resource
    private MetaEventMapper metaEventMapper;

    public List<MetaEvent> getEventsByAppIdFromDb(String appId) {
        LambdaQueryWrapper<MetaEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaEvent::getAppId, appId);

        return metaEventMapper.selectList(wrapper);
    }

    public MetaEvent getByName(String appId, String eventName) {
        LambdaQueryWrapper<MetaEvent> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(MetaEvent::getAppId, appId);
        queryWrapper.eq(MetaEvent::getEventName, eventName);

        return metaEventMapper.selectOne(queryWrapper);
    }
}