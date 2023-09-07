package com.admin.server.facade;

import com.admin.server.error.ErrorCodeEnum;
import com.admin.server.service.IAttributeService;
import com.admin.server.service.IMetaEventAttributeService;
import com.admin.server.service.IMetaEventService;
import com.api.common.bo.Attribute;
import com.api.common.bo.MetaEvent;
import com.api.common.bo.MetaEventAttribute;
import com.api.common.error.ResponseException;
import com.api.common.param.admin.CreateMetaEventAttributeParam;
import com.api.common.vo.admin.MetaEventAttributePageVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MetaFacade {

    @Resource
    private IMetaEventService metaEventService;

    @Resource
    private IMetaEventAttributeService metaEventAttributeService;

    @Resource
    private IAttributeService attributeService;

    @Transactional(rollbackFor = Exception.class)
    public void createMetaEventAttribute(CreateMetaEventAttributeParam param) {
        MetaEvent metaEvent = metaEventService.selectByAppId(param.getAppId(), param.getEventName());
        if (Objects.isNull(metaEvent)) {
            throw new ResponseException(ErrorCodeEnum.META_EVENT_NOT_EXIST.getCode(), ErrorCodeEnum.META_EVENT_NOT_EXIST.getMsg());
        }


    }

    public MetaEventAttributePageVo getMetaEventAttributes(String appId, String eventName, Integer pageNum, Integer pageSize) {
        IPage<MetaEventAttribute> metaEventAttributePage = metaEventAttributeService.queryMetaEventAttributes(appId, eventName, pageNum, pageSize);
        if (metaEventAttributePage == null || CollectionUtils.isEmpty(metaEventAttributePage.getRecords())) {
            return null;
        }

        List<MetaEventAttribute> metaEventAttributes = metaEventAttributePage.getRecords();
        List<String> attributeNames = metaEventAttributes.stream().map(MetaEventAttribute::getEventAttribute).collect(Collectors.toList());

        List<Attribute> attributes = attributeService.queryByName(attributeNames, appId);


        return null;
    }
}
