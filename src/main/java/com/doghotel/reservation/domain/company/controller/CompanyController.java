package com.doghotel.reservation.domain.company.controller;

import com.doghotel.reservation.domain.company.dto.CompanyResponseDto;
import com.doghotel.reservation.domain.company.dto.CompanySignUpDto;
import com.doghotel.reservation.domain.company.dto.CompanyUpdateDto;
import com.doghotel.reservation.domain.company.service.CompanyService;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/v1/company")
@RequiredArgsConstructor
public class CompanyController {
    private final CompanyService companyService;

    @PostMapping("/account")
    public ResponseEntity signUpCompany(@RequestBody CompanySignUpDto request) {
        String companyName = companyService.companySignUp(request);
        return new ResponseEntity(companyName, HttpStatus.OK);
    }

    @PatchMapping
    public ResponseEntity updateCompany(@AuthenticationPrincipal CustomUserDetails userDetails,
                                        @RequestPart(value = "dto") CompanyUpdateDto dto) throws IOException {
        String email = userDetails.getUsername();
        String companyName = companyService.updateCompany(dto,email);

        return new ResponseEntity(companyName, HttpStatus.OK);
    }

    @PatchMapping("/profile")
    public String updateCompanyImg(@AuthenticationPrincipal CustomUserDetails userDetails,
                                   @RequestPart(value = "file") MultipartFile file) throws IOException {
        String email = userDetails.getUsername();
        String filename = companyService.updateCompanyImg(email,file);

        return filename;
    }

    @GetMapping("/profile")
    public ResponseEntity profileCompany(@AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();

        CompanyResponseDto dto = companyService.getCompany(email);

        return new ResponseEntity(dto, HttpStatus.OK);
    }


}
