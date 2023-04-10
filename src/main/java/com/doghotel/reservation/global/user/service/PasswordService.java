package com.doghotel.reservation.global.user.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Transactional
@Service
@RequiredArgsConstructor
public class PasswordService {
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    public void changePassword(String email, String password) {
        Optional<Customer> customer = customerRepository.findByEmail(email);
        Optional<Company> company = companyRepository.findByEmail(email);

        if(customer.isPresent()) {
            Customer customer1 = customer.get();
            customer1.updatePassword(passwordEncoder.encode(password));
            customerRepository.save(customer1);
        } else if(company.isPresent()) {
            Company company1 = company.get();
            company1.updatePassword(passwordEncoder.encode(password));
            companyRepository.save(company1);
        }
    }
}
