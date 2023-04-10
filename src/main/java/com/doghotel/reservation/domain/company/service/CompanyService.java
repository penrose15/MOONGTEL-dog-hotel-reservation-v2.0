package com.doghotel.reservation.domain.company.service;

import com.doghotel.reservation.domain.company.dto.CompanyResponseDto;
import com.doghotel.reservation.domain.company.dto.CompanySignUpDto;
import com.doghotel.reservation.domain.company.dto.CompanyUpdateDto;
import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import com.doghotel.reservation.global.exception.BusinessLogicException;
import com.doghotel.reservation.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

import static com.doghotel.reservation.global.exception.ExceptionCode.COMPANY_ID_NOT_MATCH;
import static com.doghotel.reservation.global.exception.ExceptionCode.COMPANY_NOT_FOUND;

@Transactional
@Service
@RequiredArgsConstructor
public class CompanyService {
    private final CompanyRepository companyRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final AWSS3Service awss3Service;

    public String companySignUp(CompanySignUpDto request) {
        request.encodePassword(passwordEncoder.encode(request.getPassword()));

        Company company = request.toEntity();
        String email = company.getEmail();

        company = companyRepository.save(company);

        return company.getCompanyName();
    }

    public String updateCompany(CompanyUpdateDto dto, String email, Long companyId) {

        Company company = findCompanyByEmail(email);
        if(company.getCompanyId() != companyId) {
            throw new BusinessLogicException(COMPANY_ID_NOT_MATCH);
        }

        company = company.updateCompany(dto);
        company = companyRepository.save(company);

        return company.getCompanyName();
    }

    public String updateCompanyImg(String email, MultipartFile file) throws IOException {
        Company company = findCompanyByEmail(email);
        String originalFileName = awss3Service.originalFileName(file);
        String filename = awss3Service.filename(originalFileName);
        String url = awss3Service.uploadFile(file);

        company.updateCompanyImg(filename, url);

        return filename;
    }

    private Company findCompanyByEmail(String email) {
        return companyRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(COMPANY_NOT_FOUND));
    }


    public CompanyResponseDto getCompany(String email) {
        Company company = findCompanyByEmail(email);

        return CompanyResponseDto.of(company);
    }

    public void deleteCompany() {

    }

}
