package com.report.sink.dao;

import com.api.common.bo.MetaEvent;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.report.sink.mapper.MetaEventMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

/**
 * @author heqin
 */
@Component
public class MetaEventDao extends ServiceImpl<MetaEventMapper, MetaEvent> {

    @Resource
    private MetaEventMapper metaEventMapper;

    public List<MetaEvent> getEventsByAppIdFromDb(String appId) {
        if (StringUtils.isBlank(appId)) {
            return Collections.emptyList();
        }

        LambdaQueryWrapper<MetaEvent> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(MetaEvent::getAppId, appId);

        return metaEventMapper.selectList(wrapper);
    }


}