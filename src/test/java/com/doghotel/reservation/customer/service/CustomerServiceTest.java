package com.doghotel.reservation.customer.service;

import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.dto.CustomerProfileViewResponseDto;
import com.doghotel.reservation.domain.customer.dto.CustomerSignUpDto;
import com.doghotel.reservation.domain.customer.dto.CustomerUpdateRequestDto;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.customer.service.CustomerService;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CustomerServiceTest {
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AWSS3Service awss3Service;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void signUpCustomerTest() {
        CustomerSignUpDto request = CustomerSignUpDto.builder()
                .email("customer@gmail.com")
                .password("1234abcd!")
                .username("test customer")
                .phone("010-1111-2222")
                .build();
        Customer customer = request.toEntity();
        String encodedPassword = "encodedPassword";

        doReturn(encodedPassword)
                .when(passwordEncoder).encode(anyString());
        doReturn(customer)
                .when(customerRepository).save(any(Customer.class));
        String name = customerService.signUpCustomer(request);

        assertEquals(name, "test customer");
    }

    @Test
    void updateCustomerTest() {
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("password1234!")
                .username("customer")
                .phone("010-1111-2222")
                .build();
        String email = "customer@gmail.com";
        CustomerUpdateRequestDto dto = CustomerUpdateRequestDto.builder()
                .username("update username")
                .phone("010-2222-3333")
                .build();
        customer = customer.updateCustomer(dto);
        doReturn(Optional.of(customer))
                .when(customerRepository).findByEmail(email);
        doReturn(customer)
                .when(customerRepository).save(customer);

        String customerName = customerService.updateCustomer(email, dto, anyLong());
        assertEquals(customerName, "update username");
    }

    @Test
    void updateCustomerProfileTest() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/test.png");
        String email = "customer@gmail.com";

        String name = "image";
        String originalFilename = "test.png";

        String url = "https://s3.com";

        MultipartFile file = new MockMultipartFile(name,
                originalFilename,
                "png",
                fileInputStream);

        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("password1234!")
                .username("customer")
                .phone("010-1111-2222")
                .build();
        doReturn(Optional.of(customer))
                .when(customerRepository).findByEmail(email);

        doReturn(originalFilename)
                .when(awss3Service).originalFileName(file);
        doReturn(name)
                .when(awss3Service).filename(originalFilename);
        doReturn(url)
                .when(awss3Service).uploadFile(file);
        customer.updateCustomerProfile(url, name);

        String filename = customerService.updateCustomerProfile(email, file);

        assertEquals(filename, "image");

    }

    @Test
    void getCustomerProfileTest() {
        String email = "customer@gmail.com";
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("password1234!")
                .username("customer")
                .phone("010-1111-2222")
                .build();

        doReturn(Optional.of(customer))
                .when(customerRepository).findByEmail(email);

        CustomerProfileViewResponseDto response = customerService.getCustomerProfile(email, anyLong());

        assertEquals(response.getUsername(), "customer");
    }
}
