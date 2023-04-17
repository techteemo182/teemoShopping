package com.teemo.shopping.core.exception;

import lombok.Getter;

@Getter
public class ServiceException extends RuntimeException {
    private final int code;
    protected ServiceException(String message) {
        this(message, -1);
    }
    protected ServiceException(String message, int code) {
        super(message);
        this.code = code;
    }
    public static ServiceException of(String message) {
        return new ServiceException(message);
    }
    public static ServiceException of(String message, int code) {
        return new ServiceException(message, code);
    }
}
