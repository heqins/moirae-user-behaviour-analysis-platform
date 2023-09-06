package com.admin.server.interceptor;

import cn.dev33.satoken.exception.NotLoginException;
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

    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public CommonResponse<Void> handleException(NotLoginException e) {
        log.error("ErrorInterceptor NotLoginException error", e);

        CommonResponse<Void> response = new CommonResponse<>();
        response.setCode(ResponseStatusEnum.UNAUTHORIZED.getCode());
        response.setMsg(ResponseStatusEnum.UNAUTHORIZED.getMsg());

        return response;
    }
}
