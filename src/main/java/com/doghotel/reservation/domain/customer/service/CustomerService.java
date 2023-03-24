package com.doghotel.reservation.domain.customer.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.dto.CustomerProfileViewResponseDto;
import com.doghotel.reservation.domain.customer.dto.CustomerSignUpDto;
import com.doghotel.reservation.domain.customer.dto.CustomerUpdateRequestDto;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.review.dto.ReviewInfoDto;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@RequiredArgsConstructor
@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final AWSS3Service awss3Service;
    private final PasswordEncoder passwordEncoder;

    public String signUpCustomer(CustomerSignUpDto dto) {
        dto.encodePassword(passwordEncoder.encode(dto.getPassword()));

        Customer customer = dto.toEntity();
        verifyingEmail(customer.getEmail());

        customer = customerRepository.save(customer);

        return customer.getUsername();

    }
    public String updateCustomer(String email,  CustomerUpdateRequestDto request) throws IOException {
        Customer customer = findByEmail(email);

        customer = customer.updateCustomer(request);
        customer = customerRepository.save(customer);

        return customer.getUsername();
    }

    public String updateCustomerProfile(String email, MultipartFile file) throws IOException {
        Customer customer = findByEmail(email);
        String originalFilename = awss3Service.originalFileName(file);
        String filename = awss3Service.filename(originalFilename);
        String url = awss3Service.uploadFile(file);

        customer.updateCustomerProfile(url, filename);

        return filename;
    }


    public CustomerProfileViewResponseDto getCustomerProfile(String email) {
        Customer customer = findByEmail(email);
        List<ReviewInfoDto> reviewInfoDtoList = new ArrayList<>(); // 목업 데이터

        return CustomerProfileViewResponseDto.of(customer, reviewInfoDtoList);
    }

    public void verifyingEmail(String email) {
        Optional<Company> company = companyRepository.findByEmail(email);
        Optional<Customer> customer = customerRepository.findByEmail(email);

        if(company.isPresent() || customer.isPresent()) {
            throw new IllegalArgumentException("중복되는 이메일");
        }
    }


    private Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException(" 존재하지 않는 유저"));
    }
}
