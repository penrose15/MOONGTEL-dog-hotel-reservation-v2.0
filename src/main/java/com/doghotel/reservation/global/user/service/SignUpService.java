package com.doghotel.reservation.global.user.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SignUpService {
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;

    public void verifyDuplicateEmail(String email) {
        Optional<Customer> customerOptional = customerRepository.findByEmail(email);
        Optional<Company> companyOptional = companyRepository.findByEmail(email);

        if(customerOptional.isPresent() || companyOptional.isPresent()) {
            throw new IllegalArgumentException();
        }
    }


}
