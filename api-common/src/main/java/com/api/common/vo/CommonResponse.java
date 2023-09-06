package com.api.common.vo;

import com.api.common.enums.ResponseStatusEnum;
import lombok.Data;

@Data
public class CommonResponse<T> {

    private Integer code;

    private String msg;

    private T data;

    public static <T> CommonResponse<T> ofSuccess(T data) {
        CommonResponse<T> response = new CommonResponse<>();
        response.setData(data);
        response.setMsg(ResponseStatusEnum.SUCCESS.getMsg());
        response.setCode(ResponseStatusEnum.SUCCESS.getCode());

        return response;
    }

    public static <T> CommonResponse<T> ofSuccess() {
        CommonResponse<T> response = new CommonResponse<>();

        response.setMsg(ResponseStatusEnum.SUCCESS.getMsg());
        response.setCode(ResponseStatusEnum.SUCCESS.getCode());

        return response;
    }
}
