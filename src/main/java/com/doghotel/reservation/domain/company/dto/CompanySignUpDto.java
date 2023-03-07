package com.doghotel.reservation.domain.company.dto;

import com.doghotel.reservation.domain.company.entity.Company;
import lombok.Getter;

@Getter
public class CompanySignUpDto {

    private String email;
    private String password;
    private String companyName;
    private String address;
    private String detailAddress;
    private String representativeNumber;

    public Company toEntity() {
        return Company.builder()
                .email(this.email)
                .password(this.password)
                .companyName(this.companyName)
                .address(this.address)
                .detailAddress(this.detailAddress)
                .representativeNumber(this.representativeNumber)
                .build();
    }

    public void encodePassword(String password) {
        this.password = password;
    }
}
