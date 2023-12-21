package com.api.common.error;

public class SinkErrorException extends RuntimeException{

    private String errorMessage;

    private String errorHandling;

    public SinkErrorException(String message, String errorHandling) {
        super(message);

        this.errorHandling = errorHandling;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }

    public void setErrorMessage(String message) {
        this.errorMessage = message;
    }

    public String getErrorHandling() {
        return errorHandling;
    }

    public void setErrorHandling(String errorHandling) {
        this.errorHandling = errorHandling;
    }
}
