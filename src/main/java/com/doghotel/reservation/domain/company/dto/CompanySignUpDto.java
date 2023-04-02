package com.doghotel.reservation.domain.company.dto;

import com.doghotel.reservation.domain.company.entity.Company;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Validated
public class CompanySignUpDto {

    @Email
    private String email;
    @NotBlank
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}",
            message = "비밀번호는 영어, 숫자, 특수문자로 8에서 16자리로 구성되어야 합니다.")
    private String password;
    @NotBlank
    private String companyName;
    @NotBlank
    private String address;
    private String detailAddress;
    @NotBlank
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
