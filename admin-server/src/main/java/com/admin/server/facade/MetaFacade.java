package com.admin.server.facade;

import com.admin.server.error.ErrorCodeEnum;
import com.admin.server.helper.DorisHelper;
import com.admin.server.model.domain.MetaEventAttributeUtil;
import com.admin.server.model.dto.DbColumnValueDto;
import com.admin.server.service.IAppService;
import com.admin.server.service.IMetaEventAttributeService;
import com.admin.server.service.IMetaEventService;
import com.admin.server.utils.MyPageUtil;
import com.admin.server.model.bo.App;
import com.admin.server.model.bo.MetaEventAttribute;
import com.admin.server.model.bo.MetaEvent;
import com.api.common.constant.ConfigConstant;
import com.api.common.enums.AttributeDataTypeEnum;
import com.api.common.enums.AttributeTypeEnum;
import com.api.common.enums.MetaEventStatusEnum;
import com.api.common.error.ResponseException;
import com.api.common.model.param.admin.*;
import com.api.common.model.vo.PageVo;
import com.api.common.model.vo.admin.*;
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
    private IAppService appService;

    @Resource
    private DorisHelper dorisHelper;

    @Transactional(rollbackFor = Exception.class)
    public void createMetaEventAttribute(CreateMetaEventAttributeParam param) {
        MetaEvent metaEvent = metaEventService.selectByAppId(param.getAppId(), param.getEventName());
        if (Objects.isNull(metaEvent)) {
            throw new ResponseException(ErrorCodeEnum.META_EVENT_NOT_EXIST.getCode(), ErrorCodeEnum.META_EVENT_NOT_EXIST.getMsg());
        }

        List<MetaEventAttribute> metaEventAttributes = param.getAttributes()
                .stream()
                .map(value -> transferFromAttributeParam(param.getAppId(), param.getEventName(), value))
                .collect(Collectors.toList());

        metaEventAttributeService.batchInsertAttributes(metaEventAttributes);
    }

    private MetaEventAttribute transferFromAttributeParam(String appId, String eventName, AttributeParam attributeParam) {
        MetaEventAttribute metaEventAttribute = new MetaEventAttribute();

        String dataType = AttributeDataTypeEnum.generateDorisTypeWithLength(attributeParam.getDataType(),
                attributeParam.getLength(), attributeParam.getLimit());

        if (Objects.isNull(dataType)) {
            throw new ResponseException(ErrorCodeEnum.META_EVENT_ATTRIBUTE_CREATE_PARAM_ERROR.getCode(), ErrorCodeEnum.META_EVENT_ATTRIBUTE_CREATE_PARAM_ERROR.getMsg());
        }

        metaEventAttribute.setDataType(dataType);
        metaEventAttribute.setAttributeName(attributeParam.getAttributeName());
        metaEventAttribute.setAppId(appId);
        metaEventAttribute.setShowName(attributeParam.getShowName());
        metaEventAttribute.setEventName(eventName);
        metaEventAttribute.setAttributeType(AttributeTypeEnum.USER_CUSTOM.getStatus());

        return metaEventAttribute;
    }

    public PageVo<MetaEventAttributePageVo> getMetaEventAttributes(String appId, String eventName, Integer pageNum, Integer pageSize) {
        IPage<MetaEventAttribute> metaEventAttributePage = metaEventAttributeService.pageQueryByName(appId, eventName, null, pageNum, pageSize);
        if (metaEventAttributePage == null || CollectionUtils.isEmpty(metaEventAttributePage.getRecords())) {
            return null;
        }

        List<MetaEventAttribute> metaEventAttributes = metaEventAttributePage.getRecords();

        MetaEventAttributePageVo resVo = new MetaEventAttributePageVo();
        resVo.setEventName(eventName);

        List<MetaEventAttributeVo> metaEventAttributeVos = MetaEventAttributeUtil.transferFromAttributeBo(metaEventAttributes);
        resVo.setAttributes(metaEventAttributeVos);

        return MyPageUtil.constructPageVo(pageNum, pageSize, metaEventAttributePage.getTotal(), resVo);
    }

    public void createMetaEvent(CreateMetaEventParam param) {
        App existApp = appService.getByAppID(param.getAppId());
        if (Objects.isNull(existApp)) {
            throw new ResponseException(ErrorCodeEnum.APP_NOT_EXIST.getCode(), ErrorCodeEnum.APP_NOT_EXIST.getMsg());
        }

        metaEventService.createMetaEvent(param);
    }

    public PageVo<EventAttributePropPageVo> pageQueryMetaEventAttributeProperties(PageEventAttributePropParam param) {
        IPage<MetaEventAttribute> pageResult = metaEventAttributeService.pageQueryByName(param.getAppId(), param.getEventName(), null, param.getPageNum(), param.getPageSize());
        if (pageResult == null || CollectionUtils.isEmpty(pageResult.getRecords())) {
            return null;
        }

        EventAttributePropPageVo resVo = new EventAttributePropPageVo();
        resVo.setAppId(param.getAppId());
        resVo.setEventName(param.getEventName());

        List<EventAttributePropVo> propVos = MetaEventAttributeUtil.transferToPropVoFromAttributeBo(pageResult.getRecords());
        resVo.setProps(propVos);

        return MyPageUtil.constructPageVo(param.getPageNum(), param.getPageSize(), pageResult.getTotal(), resVo);
    }

    public PageVo<EventAttributeValuePageVo> queryEventReportValue(PageEventAttributeValueParam param) {
        MetaEvent metaEvent = metaEventService.selectByAppId(param.getAppId(), param.getEventName());
        if (metaEvent == null || !MetaEventStatusEnum.isEnable(metaEvent.getStatus())) {
            // todo: 错误码
            throw new ResponseException(ErrorCodeEnum.META_EVENT_NOT_EXIST.getCode(), ErrorCodeEnum.META_EVENT_NOT_EXIST.getMsg());
        }

        IPage<MetaEventAttribute> attributePage = metaEventAttributeService.pageQueryByName(param.getAppId(), param.getEventName(), param.getAttributeName(), param.getPageNum(), param.getPageSize());
        if (attributePage == null || CollectionUtils.isEmpty(attributePage.getRecords())) {
            throw new ResponseException(ErrorCodeEnum.META_EVENT_NOT_EXIST.getCode(), ErrorCodeEnum.META_EVENT_NOT_EXIST.getMsg());
        }

        String tableName = ConfigConstant.generateTableName(param.getAppId());

        // todo:
        List<DbColumnValueDto> dbColumnValues = dorisHelper.selectColumnValues("user_behaviour_analysis", tableName, param.getAttributeName());
        EventAttributeValuePageVo resPageVo = MetaEventAttributeUtil.transferToValuePageFromColumnValueDto(param.getAppId(), param.getEventName(), param.getAttributeName(), dbColumnValues);

        return MyPageUtil.constructPageVo(param.getPageNum(), param.getPageSize(), attributePage.getTotal(), resPageVo);
    }
}
