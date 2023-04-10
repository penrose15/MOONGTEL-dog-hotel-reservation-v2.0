package com.doghotel.reservation.global.user.dto;

import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;

@Getter
@Validated
public class EmailDto {
    @Email
    private String email;
}
