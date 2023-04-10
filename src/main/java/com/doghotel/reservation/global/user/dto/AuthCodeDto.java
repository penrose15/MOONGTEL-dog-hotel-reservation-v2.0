package com.doghotel.reservation.global.user.dto;

import lombok.Getter;

@Getter
public class AuthCodeDto {
    private String email;
    private String authCode;
}
