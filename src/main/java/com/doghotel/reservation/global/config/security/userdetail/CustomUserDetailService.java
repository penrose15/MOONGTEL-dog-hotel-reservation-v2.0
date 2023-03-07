package com.doghotel.reservation.global.config.security.userdetail;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.role.Roles;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@RequiredArgsConstructor
@Component
public class CustomUserDetailService implements UserDetailsService {

    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customerOptional = customerRepository.findByEmail(username);
        Optional<Company> companyOptional = companyRepository.findByEmail(username);


        String email;
        String password;
        Roles roles;
        if(customerOptional.isPresent()) {
            Customer customer = customerOptional.get();
            email = customer.getEmail();
            password = customer.getPassword();
            roles = customer.getRoles();
        } else if(companyOptional.isPresent()) {
            Company company = companyOptional.get();
            email = company.getEmail();
            password = company.getPassword();
            roles = company.getRoles();
        } else {
            throw new UsernameNotFoundException("존재하지 않는 유저");
        }
        return new CustomUserDetails(email, password, roles);
    }
}
