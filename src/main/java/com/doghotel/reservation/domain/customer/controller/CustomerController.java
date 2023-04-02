package com.doghotel.reservation.domain.customer.controller;

import com.doghotel.reservation.domain.customer.dto.CustomerProfileViewResponseDto;
import com.doghotel.reservation.domain.customer.dto.CustomerSignUpDto;
import com.doghotel.reservation.domain.customer.dto.CustomerUpdateRequestDto;
import com.doghotel.reservation.domain.customer.service.CustomerService;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import com.doghotel.reservation.global.dto.EmailCheckDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/v1/customer")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;

    @PostMapping("/account")
    public ResponseEntity signUpCustomer(@RequestBody CustomerSignUpDto request) {
        String username = customerService.signUpCustomer(request);

        return new ResponseEntity<>(username, HttpStatus.CREATED);
    }

    @PatchMapping("/account/{id}")
    public ResponseEntity updateCustomer(@PathVariable(name = "id") Long customerId,
                                         @AuthenticationPrincipal CustomUserDetails userDetails,
                                         @RequestBody CustomerUpdateRequestDto dto) throws IOException {
        String email = userDetails.getUsername();
        String username = customerService.updateCustomer(email, dto, customerId);

        return new ResponseEntity(username, HttpStatus.OK);
    }

    @PostMapping("/profile")
    public String updateCustomerProfileImage(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @RequestPart(value = "file")MultipartFile file) throws IOException {
        String email = userDetails.getUsername();
        String filename = customerService.updateCustomerProfile(email, file);

        return filename;
    }

    @PostMapping("/email")
    public ResponseEntity checkDuplicateEmail(@RequestBody EmailCheckDto dto) {
        try {
            customerService.verifyingEmail(dto.email);
        } catch (IllegalArgumentException e) {
            e.getMessage();
            return new ResponseEntity<>("email is duplicate",HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(HttpStatus.OK);
    }

    //임시 비밀번호 발급은 시큐리티 설정 후 추가

    @GetMapping("/profile/{id}") //고객 상세 페이지
    public ResponseEntity showCustomerProfile(@PathVariable(name = "id") Long customerId
            ,@AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        CustomerProfileViewResponseDto response = customerService.getCustomerProfile(email, customerId);

        return ResponseEntity.ok(response);
    }
}
