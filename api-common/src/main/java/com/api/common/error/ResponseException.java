package com.api.common.error;

import com.api.common.enums.ResponseStatusEnum;

public class ResponseException extends RuntimeException {

    private int errorCode;

    private String errorMessage;

    public ResponseException(int errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ResponseException(ResponseStatusEnum statusEnum) {
        super(statusEnum.getMsg());
        this.errorCode = statusEnum.getCode();
        this.errorMessage = statusEnum.getMsg();
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
