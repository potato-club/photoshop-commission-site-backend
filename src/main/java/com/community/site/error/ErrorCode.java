package com.community.site.error;

import lombok.Getter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

@Getter
@ToString
public enum ErrorCode {

    RUNTIME_EXCEPTION(HttpStatus.BAD_REQUEST, "E0001", "400 Bad Request"),
    ACCESS_DENIED_EXCEPTION(HttpStatus.UNAUTHORIZED, "E0002", "401 UnAuthorized"),
    FORBIDDEN_EXCEPTION(HttpStatus.FORBIDDEN, "E0003", "403 Forbidden"),
    NOT_FOUND_EXCEPTION(HttpStatus.NOT_FOUND, "E0004", "404 Not Found"),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "E0005", "500 Internal Server Error");

    private final HttpStatus status;
    private final String code;
    private String message;

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
