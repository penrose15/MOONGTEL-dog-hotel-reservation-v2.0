package com.doghotel.reservation.global.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailCheckDto {
    public String email;

    public EmailCheckDto(String email) {
        this.email = email;
    }
}
