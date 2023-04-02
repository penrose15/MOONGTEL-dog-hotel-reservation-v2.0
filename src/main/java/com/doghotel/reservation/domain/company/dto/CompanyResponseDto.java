package com.doghotel.reservation.domain.company.dto;

import com.doghotel.reservation.domain.company.entity.Company;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyResponseDto {
    private String companyName;
    private String email;
    private String address;
    private String detailAddress;
    private String representativeNumber;
    private String filename;
    private String imgUrl;

    public CompanyResponseDto(String companyName, String email, String address, String detailAddress, String representativeNumber, String filename, String imgUrl) {
        this.companyName = companyName;
        this.email = email;
        this.address = address;
        this.detailAddress = detailAddress;
        this.representativeNumber = representativeNumber;
        this.filename = filename;
        this.imgUrl = imgUrl;
    }

    public static CompanyResponseDto of(Company company) {
        return new CompanyResponseDto(company.getCompanyName(),
                company.getEmail(),
                company.getAddress(),
                company.getDetailAddress(),
                company.getRepresentativeNumber(),
                company.getFileName(),
                company.getImgUrl());
    }
}
