package com.admin.server.interceptor;

import cn.dev33.satoken.exception.NotLoginException;
import com.admin.server.helper.DorisHelper;
import com.api.common.enums.ResponseStatusEnum;
import com.api.common.error.ResponseException;
import com.api.common.model.vo.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
public class ErrorInterceptor {

    private final Logger logger = LoggerFactory.getLogger(DorisHelper.class);

    @ExceptionHandler(value = ResponseException.class)
    @ResponseBody
    public CommonResponse<Void> handlerException(ResponseException e) {
        logger.warn("ErrorInterceptor response error", e);

        CommonResponse<Void> response = new CommonResponse<>();
        response.setCode(e.getErrorCode());
        response.setMsg(e.getErrorMessage());

        return response;
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseBody
    public CommonResponse<Void> handlerException(MethodArgumentNotValidException e) {
        logger.warn("ErrorInterceptor valid param error", e);

        CommonResponse<Void> response = new CommonResponse<>();
        response.setCode(ResponseStatusEnum.PARAM_ERROR.getCode());
        response.setMsg(e.getMessage());

        return response;
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public CommonResponse<Void> handleException(Exception e) {
        logger.error("ErrorInterceptor unknown error", e);

        CommonResponse<Void> response = new CommonResponse<>();
        response.setCode(ResponseStatusEnum.UNKNOWN_ERROR.getCode());
        response.setMsg(ResponseStatusEnum.UNKNOWN_ERROR.getMsg());

        return response;
    }

    @ExceptionHandler(value = NotLoginException.class)
    @ResponseBody
    public CommonResponse<Void> handleException(NotLoginException e) {
        logger.warn("ErrorInterceptor NotLoginException error", e);

        CommonResponse<Void> response = new CommonResponse<>();
        response.setCode(ResponseStatusEnum.UNAUTHORIZED.getCode());
        response.setMsg(ResponseStatusEnum.UNAUTHORIZED.getMsg());

        return response;
    }
}
