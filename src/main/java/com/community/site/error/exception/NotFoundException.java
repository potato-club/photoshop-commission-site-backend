package com.community.site.error.exception;

import com.community.site.error.ErrorCode;

public class NotFoundException extends BusinessException {

    public NotFoundException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
