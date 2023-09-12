package com.admin.server.controller;

import com.admin.server.facade.MetaFacade;
import com.admin.server.service.IMetaEventAttributeService;
import com.admin.server.service.IMetaEventService;
import com.api.common.constant.ApiConstants;
import com.api.common.model.param.admin.*;
import com.api.common.model.vo.CommonResponse;
import com.api.common.model.vo.PageVo;
import com.api.common.model.vo.admin.EventAttributePropPageVo;
import com.api.common.model.vo.admin.EventAttributeValuePageVo;
import com.api.common.model.vo.admin.MetaEventAttributePageVo;
import com.api.common.model.vo.admin.MetaEventsPageVo;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/meta-data")
@Tag(name = "元数据管理")
public class MetaEventController {

    @Resource
    private IMetaEventService metaEventService;

    @Resource
    private MetaFacade metaFacade;

    @Resource
    private IMetaEventAttributeService attributeService;

    @Operation(description = "分页查询当前应用下的所有元事件")
    @GetMapping("/events")
    public CommonResponse<PageVo<MetaEventsPageVo>> pageQueryMetaEvents(@Parameter(description = "test") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                                @RequestParam String appId) {
        return CommonResponse.ofSuccess(metaEventService.queryMetaEventsByPage(pageNum, pageSize, appId));
    }

    @Operation(description = "创建元事件")
    @PostMapping("/event")
    public CommonResponse<Void> createMetaEvent(@RequestBody @Valid CreateMetaEventParam createMetaEventParam) {
        metaFacade.createMetaEvent(createMetaEventParam);
        return CommonResponse.ofSuccess();
    }

    @Operation(description = "创建元事件属性")
    @PostMapping("/event/attribute")
    public CommonResponse<Void> createMetaEventAttribute(@RequestBody @Valid CreateMetaEventAttributeParam param) {
        metaFacade.createMetaEventAttribute(param);
        return CommonResponse.ofSuccess();
    }

    @Operation(description = "更改元事件属性")
    @PostMapping("/event/attribute/update")
    public CommonResponse<Void> updateMetaEventAttribute(@RequestBody @Valid UpdateMetaEventAttributeParam param) {
        attributeService.updateMetaEventAttribute(param);
        return CommonResponse.ofSuccess();
    }

    @Operation(description = "获取分页获取指定元事件下的所有属性")
    @PostMapping("/event/{eventName}/queryAttributes")
    public CommonResponse<PageVo<MetaEventAttributePageVo>> getMetaEventAttributes(@Parameter(description = "test") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                                                   @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                                                   @RequestParam(required = true) String appId,
                                                                                   @PathVariable String eventName) {
        return CommonResponse.ofSuccess(metaFacade.getMetaEventAttributes(appId, eventName, pageNum, pageSize));
    }

    @Operation(description = "分页查询元事件下属性值")
    @PostMapping("/event/queryAttributeProperties")
    public CommonResponse<PageVo<EventAttributePropPageVo>> pageQueryMetaEventAttributeProperties(@RequestBody @Valid PageEventAttributePropParam param) {

        return CommonResponse.ofSuccess(metaFacade.pageQueryMetaEventAttributeProperties(param));
    }


    @Operation(description = "查询事件属性上报值")
    @PostMapping("/event/queryEventReportValue")
    public CommonResponse<PageVo<EventAttributeValuePageVo>> queryEventReportValue(@RequestBody @Valid PageEventAttributeValueParam param) {

        return CommonResponse.ofSuccess(metaFacade.queryEventReportValue(param));
    }

    @Operation(description = "启用元事件")
    @PostMapping("/event/{eventName}")
    public CommonResponse<Void> enableMetaEvent(@RequestParam String appId,
                                                @PathVariable String eventName) {
        metaEventService.enableMetaEvent(appId, eventName);
        return CommonResponse.ofSuccess();
    }

    @Operation(description = "关闭元事件")
    @DeleteMapping("/event/{eventName}")
    public CommonResponse<Void> disableMetaEvent(@RequestParam String appId,
                                                 @PathVariable String eventName) {
        metaEventService.disableMetaEvent(appId, eventName);
        return CommonResponse.ofSuccess();
    }
}
