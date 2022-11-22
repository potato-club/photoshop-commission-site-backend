package com.community.site.error.exception;

import com.community.site.error.ErrorCode;

public class InternerServerException extends BusinessException {

    public InternerServerException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
