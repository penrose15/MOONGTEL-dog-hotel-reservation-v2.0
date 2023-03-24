package com.doghotel.reservation.domain.customer.dto;

import com.doghotel.reservation.domain.customer.entity.Customer;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Getter
@Validated
public class CustomerSignUpDto {
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String username;
    @NotBlank
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
