package com.esun.seating.common.exception;

import lombok.Getter;

/**
 * 業務邏輯例外，用於傳遞可呈現給使用者的錯誤訊息。
 */
@Getter
public class BusinessException extends RuntimeException {

    private final String code;

    public BusinessException(String message) {
        super(message);
        this.code = "BUSINESS_ERROR";
    }

    public BusinessException(String code, String message) {
        super(message);
        this.code = code;
    }
}
