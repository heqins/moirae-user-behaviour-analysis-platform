package com.admin.server.facade;

import com.admin.server.error.ErrorCodeEnum;
import com.admin.server.service.IAttributeService;
import com.admin.server.service.IMetaAttributeRelationService;
import com.admin.server.service.IMetaEventService;
import com.admin.server.util.MyPageUtil;
import com.api.common.bo.Attribute;
import com.api.common.bo.MetaEvent;
import com.api.common.bo.MetaAttributeRelation;
import com.api.common.enums.AttributeDataTypeEnum;
import com.api.common.enums.AttributeTypeEnum;
import com.api.common.error.ResponseException;
import com.api.common.param.admin.AttributeParam;
import com.api.common.param.admin.CreateMetaEventAttributeParam;
import com.api.common.vo.PageVo;
import com.api.common.vo.admin.MetaAttributeRelationPageVo;
import com.api.common.vo.admin.MetaAttributeRelationVo;
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
    private IMetaAttributeRelationService metaAttributeRelationService;

    @Resource
    private IAttributeService attributeService;

    @Transactional(rollbackFor = Exception.class)
    public void createMetaEventAttribute(CreateMetaEventAttributeParam param) {
        MetaEvent metaEvent = metaEventService.selectByAppId(param.getAppId(), param.getEventName());
        if (Objects.isNull(metaEvent)) {
            throw new ResponseException(ErrorCodeEnum.META_EVENT_NOT_EXIST.getCode(), ErrorCodeEnum.META_EVENT_NOT_EXIST.getMsg());
        }

        List<Attribute> attributes = param.getAttributes()
                .stream()
                .map(value -> transferFromAttributeParam(param.getAppId(), value))
                .collect(Collectors.toList());

        List<MetaAttributeRelation> eventAttributeRelation = param.getAttributes()
                .stream()
                .map(value -> transferFromAttributeParam(param.getAppId(), param.getEventName(), value.getAttributeName()))
                .collect(Collectors.toList());

        metaAttributeRelationService.batchInsertAttributes(eventAttributeRelation);
        attributeService.batchInsertAttributes(attributes);
    }

    private Attribute transferFromAttributeParam(String appId, AttributeParam attributeParam) {
        Attribute attribute = new Attribute();

        String dataType = AttributeDataTypeEnum.generateDorisTypeWithLength(attributeParam.getDataType(),
                attributeParam.getLength(), attributeParam.getLimit());

        attribute.setDataType(dataType);
        attribute.setAttributeName(attributeParam.getAttributeName());
        attribute.setAppId(appId);
        attribute.setShowName(attributeParam.getShowName());
        attribute.setAttributeType(AttributeTypeEnum.USER_CUSTOM.getStatus());

        return attribute;
    }

    private MetaAttributeRelation transferFromAttributeParam(String appId, String eventName, String attributeName) {
        MetaAttributeRelation metaAttributeRelation = new MetaAttributeRelation();
        metaAttributeRelation.setEventName(eventName);
        metaAttributeRelation.setEventAttribute(attributeName);
        metaAttributeRelation.setAppId(appId);

        return metaAttributeRelation;
    }

    public PageVo<MetaAttributeRelationPageVo> getMetaEventAttributes(String appId, String eventName, Integer pageNum, Integer pageSize) {
        IPage<MetaAttributeRelation> metaEventAttributePage = metaAttributeRelationService.queryMetaEventAttributes(appId, eventName, pageNum, pageSize);
        if (metaEventAttributePage == null || CollectionUtils.isEmpty(metaEventAttributePage.getRecords())) {
            return null;
        }

        List<MetaAttributeRelation> metaAttributeRelations = metaEventAttributePage.getRecords();
        List<String> attributeNames = metaAttributeRelations.stream().map(MetaAttributeRelation::getEventAttribute).collect(Collectors.toList());

        List<Attribute> attributes = attributeService.queryByName(attributeNames, appId);
        MetaAttributeRelationPageVo resVo = new MetaAttributeRelationPageVo();
        resVo.setEventName(eventName);

        List<MetaAttributeRelationVo> metaAttributeRelationVos = MetaAttributeRelationVo.transferFromAttributeBo(attributes);
        resVo.setAttributes(metaAttributeRelationVos);

        return MyPageUtil.constructPageVo(pageNum, pageSize, metaEventAttributePage.getTotal(), resVo);
    }
}
