package com.admin.server.controller;

import com.admin.server.service.IMetaEventService;
import com.api.common.constant.ApiConstants;
import com.api.common.vo.CommonResponse;
import com.api.common.vo.MetaEventsPageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/meta-data")
@Api(tags = "元数据管理")
public class MetaEventController {

    @Resource
    private IMetaEventService metaEventService;

    @ApiOperation(value = "分页查询当前应用下的所有元事件")
    @GetMapping("/events/{appId}")
    public CommonResponse<MetaEventsPageVo> pageQueryMetaEvents(@ApiParam(value = "test") @RequestParam(required = false, defaultValue = "1") Integer pageNum,
                                                                @RequestParam(required = false, defaultValue = "10") Integer pageSize,
                                                                @PathVariable String appId) {
        return CommonResponse.ofSuccess(metaEventService.queryMetaEventsByPage(pageNum, pageSize, appId));
    }

    @ApiOperation(value = "启用应用元事件")
    @PostMapping("/event/{appId}/{eventName}")
    public CommonResponse enableMetaEvent(@PathVariable String appId,
                                          @PathVariable String eventName) {
        metaEventService.enableMetaEvent(appId, eventName);
        return CommonResponse.ofSuccess();
    }

    @ApiOperation(value = "关闭应用元事件")
    @DeleteMapping("/event/{appId}/{eventName}")
    public CommonResponse disableMetaEvent(@PathVariable String appId,
                                           @PathVariable String eventName) {
        metaEventService.disableMetaEvent(appId, eventName);
        return CommonResponse.ofSuccess();
    }
}
