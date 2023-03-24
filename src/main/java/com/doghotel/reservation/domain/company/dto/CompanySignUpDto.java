package com.doghotel.reservation.domain.company.dto;

import com.doghotel.reservation.domain.company.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public class CompanySignUpDto {

    private String email;
    private String password;
    private String companyName;
    private String address;
    private String detailAddress;
    private String representativeNumber;

    @Builder // for test
    public CompanySignUpDto(String email, String password, String companyName, String address, String detailAddress, String representativeNumber) {
        this.email = email;
        this.password = password;
        this.companyName = companyName;
        this.address = address;
        this.detailAddress = detailAddress;
        this.representativeNumber = representativeNumber;
    }

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
