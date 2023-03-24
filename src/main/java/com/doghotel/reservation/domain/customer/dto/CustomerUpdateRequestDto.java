package com.doghotel.reservation.domain.customer.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class CustomerUpdateRequestDto {
    private String username;
    private String phone;

    @Builder
    public CustomerUpdateRequestDto(String username, String phone) {
        this.username = username;
        this.phone = phone;
    }
}
