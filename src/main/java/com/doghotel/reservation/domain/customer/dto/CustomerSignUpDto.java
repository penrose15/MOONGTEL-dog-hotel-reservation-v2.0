package com.doghotel.reservation.domain.customer.dto;

import com.doghotel.reservation.domain.customer.entity.Customer;
import lombok.Builder;
import lombok.Getter;

@Getter
public class CustomerSignUpDto {
    private String email;
    private String password;
    private String username;
    private String phone;

    @Builder
    public CustomerSignUpDto(String email, String password, String username, String phone) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.phone = phone;
    }

    public Customer toEntity() {
        return Customer.builder()
                .email(this.email)
                .password(this.password)
                .username(this.username)
                .phone(this.phone)
                .build();
    }

    public void encodePassword(String password) {
        this.password = password;
    }
}
