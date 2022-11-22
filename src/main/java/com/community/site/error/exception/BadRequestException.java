package com.community.site.error.exception;

import com.community.site.error.ErrorCode;

public class BadRequestException extends BusinessException {

    public BadRequestException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
