package com.admin.server.service.impl;

import com.admin.server.dao.MetaEventDao;
import com.admin.server.service.ICacheService;
import com.admin.server.service.IMetaEventService;
import com.api.common.bo.MetaEvent;
import com.api.common.enums.MetaEventStatusEnum;
import com.api.common.vo.admin.MetaEventVo;
import com.api.common.vo.admin.MetaEventsPageVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author heqin
 */
@Service
@Slf4j
public class MetaEventServiceImpl implements IMetaEventService {

    @Resource
    private MetaEventDao metaEventDao;

    @Resource(name = "redisCacheService")
    private ICacheService redisCache;

    @Override
    public MetaEventsPageVo queryMetaEventsByPage(Integer pageNum, Integer pageSize, String appId) {
        if (StringUtils.isBlank(appId)) {
            return null;
        }

        IPage<MetaEvent> metaEventPage = metaEventDao.selectPageByAppId(appId, pageNum, pageSize);

        MetaEventsPageVo resultVo = new MetaEventsPageVo();
        resultVo.setCurrentNum(pageNum);
        resultVo.setPageSize(pageSize);
        resultVo.setTotal(metaEventPage.getTotal());
        resultVo.setAppId(appId);

        if (CollectionUtils.isEmpty(metaEventPage.getRecords())) {
            return resultVo;
        }

        List<MetaEventVo> metaEventVos = MetaEventVo.transferFromEventBo(metaEventPage.getRecords());
        resultVo.setEvents(metaEventVos);

        return resultVo;
    }

    @Override
    public void enableMetaEvent(String appId, String eventName) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(eventName)) {
            return;
        }

        metaEventDao.changeMetaEventStatus(appId, eventName, MetaEventStatusEnum.ENABLE.getStatus());
        redisCache.setMetaEventCache(appId, eventName, MetaEventStatusEnum.ENABLE.getStatus());
    }

    @Override
    public void disableMetaEvent(String appId, String eventName) {
        if (StringUtils.isBlank(appId) || StringUtils.isBlank(eventName)) {
            return;
        }

        metaEventDao.changeMetaEventStatus(appId, eventName, MetaEventStatusEnum.DISABLE.getStatus());
        redisCache.setMetaEventCache(appId, eventName, MetaEventStatusEnum.DISABLE.getStatus());
    }
}
