package com.doghotel.reservation.domain.company.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CompanyUpdateDto {
    private String companyName;
    private String address;
    private String detailAddress;
    private String representativeNumber;

    @Builder //for test
    public CompanyUpdateDto(String companyName, String address, String detailAddress, String representativeNumber) {
        this.companyName = companyName;
        this.address = address;
        this.detailAddress = detailAddress;
        this.representativeNumber = representativeNumber;
    }
}
