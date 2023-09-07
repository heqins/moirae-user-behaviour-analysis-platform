package com.admin.server.service.impl;

import com.admin.server.dao.MetaEventDao;
import com.admin.server.error.ErrorCodeEnum;
import com.admin.server.service.ICacheService;
import com.admin.server.service.IMetaEventService;
import com.admin.server.util.MyPageUtil;
import com.api.common.bo.MetaEvent;
import com.api.common.enums.MetaEventStatusEnum;
import com.api.common.error.ResponseException;
import com.api.common.param.admin.CreateMetaEventParam;
import com.api.common.vo.PageVo;
import com.api.common.vo.admin.MetaEventVo;
import com.api.common.vo.admin.MetaEventsPageVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;

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
    public PageVo<MetaEventsPageVo> queryMetaEventsByPage(Integer pageNum, Integer pageSize, String appId) {
        if (StringUtils.isBlank(appId)) {
            return null;
        }

        IPage<MetaEvent> metaEventPage = metaEventDao.selectPageByAppId(appId, pageNum, pageSize);

        MetaEventsPageVo resultVo = new MetaEventsPageVo();
        resultVo.setAppId(appId);

        if (CollectionUtils.isEmpty(metaEventPage.getRecords())) {
            return MyPageUtil.constructPageVo(pageNum, pageSize, 0L, resultVo);
        }

        List<MetaEventVo> metaEventVos = MetaEventVo.transferFromEventBo(metaEventPage.getRecords());
        resultVo.setEvents(metaEventVos);

        return MyPageUtil.constructPageVo(pageNum, pageSize, metaEventPage.getTotal(), resultVo);
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

    @Override
    public void createMetaEvent(CreateMetaEventParam createMetaEventParam) {
        MetaEvent existMetaEvent = metaEventDao.selectByName(createMetaEventParam.getAppId(), createMetaEventParam.getEventName());
        if (Objects.nonNull(existMetaEvent)) {
            throw new ResponseException(ErrorCodeEnum.META_EVENT_EXIST.getCode(), ErrorCodeEnum.META_EVENT_EXIST.getMsg());
        }

        MetaEvent metaEvent = transferFromCreateParam(createMetaEventParam);
        metaEventDao.save(metaEvent);
    }

    @Override
    public MetaEvent selectByAppId(String appId, String eventName) {
        return metaEventDao.selectByName(appId, eventName);
    }

    private MetaEvent transferFromCreateParam(CreateMetaEventParam param) {
        if (Objects.isNull(param)) {
            return null;
        }

        MetaEvent metaEvent = new MetaEvent();
        metaEvent.setAppId(param.getAppId().trim());
        metaEvent.setEventName(param.getEventName().trim());
        metaEvent.setShowName(param.getShowName());

        return metaEvent;
    }
}
