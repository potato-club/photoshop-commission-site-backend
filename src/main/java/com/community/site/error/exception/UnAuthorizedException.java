package com.community.site.error.exception;

import com.community.site.error.ErrorCode;

public class UnAuthorizedException extends BusinessException {

    public UnAuthorizedException(String message, ErrorCode errorCode) {
        super(message, errorCode);
    }
}
