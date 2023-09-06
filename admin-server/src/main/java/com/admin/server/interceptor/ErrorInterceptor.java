package com.admin.server.interceptor;

import com.api.common.enums.ResponseStatusEnum;
import com.api.common.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class ErrorInterceptor {

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResponse<Void> handleException(Exception e) {
        log.error("ErrorInterceptor unknown error", e);

        CommonResponse<Void> response = new CommonResponse<>();
        response.setCode(ResponseStatusEnum.UNKNOWN_ERROR.getCode());
        response.setMsg(ResponseStatusEnum.UNKNOWN_ERROR.getMsg());

        return response;
    }
}
