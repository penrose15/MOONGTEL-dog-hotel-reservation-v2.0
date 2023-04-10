package com.doghotel.reservation.domain.company.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.global.exception.BusinessLogicException;
import com.doghotel.reservation.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class CompanyVerifyService {
    private final CompanyRepository companyRepository;

    public Company verifyingEmail(String email) {
        return companyRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMPANY_NOT_FOUND));
    }

    public Company findById(Long companyId) {
        return companyRepository.findById(companyId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMPANY_NOT_FOUND));
    }
}
