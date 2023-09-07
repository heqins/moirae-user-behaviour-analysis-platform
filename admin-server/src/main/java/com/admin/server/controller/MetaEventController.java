package com.admin.server.controller;

import com.admin.server.facade.MetaFacade;
import com.admin.server.service.IMetaEventService;
import com.api.common.constant.ApiConstants;
import com.api.common.param.admin.CreateMetaEventAttributeParam;
import com.api.common.param.admin.CreateMetaEventParam;
import com.api.common.vo.CommonResponse;
import com.api.common.vo.admin.MetaEventsPageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/meta-data")
@Api(tags = "元数据管理")
public class MetaEventController {

    @Resource
    private IMetaEventService metaEventService;

    @Resource
    private MetaFacade metaFacade;

    @ApiOperation(value = "分页查询当前应用下的所有元事件")
    @GetMapping("/events")
    public CommonResponse<MetaEventsPageVo> pageQueryMetaEvents(@ApiParam(value = "test") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                                @RequestParam String appId) {
        return CommonResponse.ofSuccess(metaEventService.queryMetaEventsByPage(pageNum, pageSize, appId));
    }

    @ApiOperation(value = "创建元事件")
    @PostMapping("/event")
    public CommonResponse<Void> createMetaEvent(@RequestBody @Valid CreateMetaEventParam createMetaEventParam) {
        metaEventService.createMetaEvent(createMetaEventParam);
        return CommonResponse.ofSuccess();
    }

    @ApiOperation(value = "创建元事件属性")
    @PostMapping("/event/attribute")
    public CommonResponse<Void> createMetaEventAttribute(@RequestBody @Valid CreateMetaEventAttributeParam param) {
        metaFacade.createMetaEventAttribute(param);
        return CommonResponse.ofSuccess();
    }

    @ApiOperation(value = "启用元事件")
    @PostMapping("/event/{eventName}")
    public CommonResponse<Void> enableMetaEvent(@RequestParam String appId,
                                                @PathVariable String eventName) {
        metaEventService.enableMetaEvent(appId, eventName);
        return CommonResponse.ofSuccess();
    }

    @ApiOperation(value = "关闭元事件")
    @DeleteMapping("/event/{eventName}")
    public CommonResponse<Void> disableMetaEvent(@RequestParam String appId,
                                                 @PathVariable String eventName) {
        metaEventService.disableMetaEvent(appId, eventName);
        return CommonResponse.ofSuccess();
    }


}
