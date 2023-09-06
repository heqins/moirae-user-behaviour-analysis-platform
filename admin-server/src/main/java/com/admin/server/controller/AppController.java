package com.admin.server.controller;

import com.admin.server.service.IAppService;
import com.admin.server.service.IMetaEventService;
import com.api.common.constant.ApiConstants;
import com.api.common.param.admin.CreateAppParam;
import com.api.common.vo.CommonResponse;
import com.api.common.vo.MetaEventsPageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @author heqin
 */
@RestController
@RequestMapping(value = ApiConstants.ADMIN_SERVER_API_PREFIX + "/app")
@Api(tags = "应用管理")
public class AppController {

    @Resource
    private IAppService appService;

    @ApiOperation(value = "创建应用")
    @PostMapping("/create")
    public CommonResponse<Void> createApp(@RequestBody CreateAppParam createAppParam) {
        appService.createApp(createAppParam);
        return CommonResponse.ofSuccess();
    }

}
