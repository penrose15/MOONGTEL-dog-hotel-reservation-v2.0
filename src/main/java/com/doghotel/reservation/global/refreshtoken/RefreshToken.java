package com.doghotel.reservation.global.refreshtoken;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Id;

@Getter
@AllArgsConstructor
public class RefreshToken {
    @Id
    private String refreshToken;

    private String email;
}
