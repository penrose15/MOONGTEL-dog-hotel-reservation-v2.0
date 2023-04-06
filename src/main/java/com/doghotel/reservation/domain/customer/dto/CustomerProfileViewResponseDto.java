package com.doghotel.reservation.domain.customer.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CustomerProfileViewResponseDto {
    private String username;
    private String email;
    private String phone;
    private String profile;
    private String profile_url;

//    @Builder
    public CustomerProfileViewResponseDto(String username, String email,String phone, String profile, String profile_url) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.profile = profile;
        this.profile_url = profile_url;
    }
}
