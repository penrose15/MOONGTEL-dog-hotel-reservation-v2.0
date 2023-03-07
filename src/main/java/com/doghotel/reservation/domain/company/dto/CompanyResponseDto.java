package com.doghotel.reservation.domain.company.dto;

import com.doghotel.reservation.domain.company.entity.Company;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CompanyResponseDto {
    private String companyName;
    private String email;
    private String address;
    private String detailAddress;
    private String representativeNumber;

    @Builder
    public CompanyResponseDto(String companyName,String email, String address, String detailAddress, String representativeNumber) {
        this.companyName = companyName;
        this.email = email;
        this.address = address;
        this.detailAddress = detailAddress;
        this.representativeNumber = representativeNumber;
    }
}
