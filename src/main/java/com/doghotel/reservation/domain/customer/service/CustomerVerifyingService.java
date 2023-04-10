package com.doghotel.reservation.domain.customer.service;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.global.exception.BusinessLogicException;
import com.doghotel.reservation.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional
@RequiredArgsConstructor
@Service
public class CustomerVerifyingService {
    private final CustomerRepository customerRepository;

    public Customer findByEmail(String email) {
        return customerRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.CUSTOMER_NOT_FOUND));
    }
}
