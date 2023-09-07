package com.admin.server.facade;

import com.admin.server.error.ErrorCodeEnum;
import com.admin.server.service.IMetaEventAttributeService;
import com.admin.server.service.IMetaEventService;
import com.admin.server.util.MyPageUtil;
import com.api.common.bo.MetaEventAttribute;
import com.api.common.bo.MetaEvent;
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
    private IMetaEventAttributeService metaEventAttributeService;

    @Transactional(rollbackFor = Exception.class)
    public void createMetaEventAttribute(CreateMetaEventAttributeParam param) {
        MetaEvent metaEvent = metaEventService.selectByAppId(param.getAppId(), param.getEventName());
        if (Objects.isNull(metaEvent)) {
            throw new ResponseException(ErrorCodeEnum.META_EVENT_NOT_EXIST.getCode(), ErrorCodeEnum.META_EVENT_NOT_EXIST.getMsg());
        }

        List<MetaEventAttribute> metaEventAttributes = param.getAttributes()
                .stream()
                .map(value -> transferFromAttributeParam(param.getAppId(), value))
                .collect(Collectors.toList());

        metaEventAttributeService.batchInsertAttributes(metaEventAttributes);
    }

    private MetaEventAttribute transferFromAttributeParam(String appId, AttributeParam attributeParam) {
        MetaEventAttribute metaEventAttribute = new MetaEventAttribute();

        String dataType = AttributeDataTypeEnum.generateDorisTypeWithLength(attributeParam.getDataType(),
                attributeParam.getLength(), attributeParam.getLimit());

        metaEventAttribute.setDataType(dataType);
        metaEventAttribute.setAttributeName(attributeParam.getAttributeName());
        metaEventAttribute.setAppId(appId);
        metaEventAttribute.setShowName(attributeParam.getShowName());
        metaEventAttribute.setAttributeType(AttributeTypeEnum.USER_CUSTOM.getStatus());

        return metaEventAttribute;
    }

    public PageVo<MetaAttributeRelationPageVo> getMetaEventAttributes(String appId, String eventName, Integer pageNum, Integer pageSize) {
        IPage<MetaAttributeRelation> metaEventAttributePage = metaAttributeRelationService.queryMetaEventAttributes(appId, eventName, pageNum, pageSize);
        if (metaEventAttributePage == null || CollectionUtils.isEmpty(metaEventAttributePage.getRecords())) {
            return null;
        }

        List<MetaAttributeRelation> metaAttributeRelations = metaEventAttributePage.getRecords();
        List<String> attributeNames = metaAttributeRelations.stream().map(MetaAttributeRelation::getEventAttribute).collect(Collectors.toList());

        List<MetaEventAttribute> metaEventAttributes = metaEventAttributeService.queryByName(attributeNames, appId);
        MetaAttributeRelationPageVo resVo = new MetaAttributeRelationPageVo();
        resVo.setEventName(eventName);

        List<MetaAttributeRelationVo> metaAttributeRelationVos = MetaAttributeRelationVo.transferFromAttributeBo(metaEventAttributes);
        resVo.setAttributes(metaAttributeRelationVos);

        return MyPageUtil.constructPageVo(pageNum, pageSize, metaEventAttributePage.getTotal(), resVo);
    }
}
