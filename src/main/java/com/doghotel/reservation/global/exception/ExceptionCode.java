package com.doghotel.reservation.global.exception;

import lombok.Getter;

public enum ExceptionCode {

    INVALID_EMAIL(404,"존재하지 않는 이메일");
    @Getter
    private int status;

    @Getter
    private String message;

    ExceptionCode(int status, String message) {
        this.status = status;
        this.message = message;
    }
}
