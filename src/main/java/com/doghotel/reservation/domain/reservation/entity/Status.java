package com.doghotel.reservation.domain.reservation.entity;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum Status {
    RESERVED("RESERVED"),
    ACCEPTED("ACCEPTED"),
    VISITED("VISITED"),
    CANCELED("CANCELED");

    private String status;

    Status(String status) {
        this.status = status;
    }

    public static Status convertToStatus(String status) {
        return Arrays.stream(Status.values())
                .filter(status1 -> status1.status.equals(status))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("잘못된 입력값"));
    }
}
