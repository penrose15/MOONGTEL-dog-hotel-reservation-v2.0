package com.doghotel.reservation.company.sevice;

import com.doghotel.reservation.domain.company.dto.CompanyResponseDto;
import com.doghotel.reservation.domain.company.dto.CompanySignUpDto;
import com.doghotel.reservation.domain.company.dto.CompanyUpdateDto;
import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.company.service.CompanyService;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.post.dto.PostsResponsesDto;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CompanyServiceTest {
    @Mock
    private CompanyRepository companyRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AWSS3Service awss3Service;

    @InjectMocks
    private CompanyService companyService;

    @Test
    void signUpCompanyTest() {
        CompanySignUpDto request = CompanySignUpDto.builder()
                .email("test@test.com")
                .password("test1234")
                .companyName("Test Company")
                .address("123 Test St.")
                .detailAddress("Apt 123")
                .representativeNumber("1234567890")
                .build();
        String encodedPassword = "encodedPassword1234";
        doReturn(encodedPassword)
                .when(passwordEncoder).encode(anyString());
        Company company = request.toEntity();

        doReturn(Optional.empty())
                .when(companyRepository).findByEmail(anyString());
        doReturn(Optional.empty())
                .when(customerRepository).findByEmail(anyString());

        doReturn(company)
                .when(companyRepository).save(any(Company.class));

        String companyName = companyService.companySignUp(request);
        assertEquals(companyName, "Test Company");
    }

    @Test
    void updateCompanyTest() {
        CompanyUpdateDto request = CompanyUpdateDto.builder()
                .companyName("update companyName")
                .address("updated address")
                .detailAddress("updated detailAddress")
                .representativeNumber("1234567891")
                .build();
        String email  = "company@gmail.com";

        Company company = Company.builder()
                .email("company@gmail.com")
                .password("1234abcd!")
                .companyName("test company")
                .address("test address")
                .detailAddress("test detailAddress")
                .representativeNumber("123456789")
                .build();

        doReturn(Optional.ofNullable(company))
                .when(companyRepository).findByEmail(email);

        company = company.updateCompany(request);

        doReturn(company)
                .when(companyRepository).save(company);
        String companyName = companyService.updateCompany(request, email);
    }

    @Test
    void updateCompanyImg() throws IOException {
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/test.png");
        String email = "company@gmail.com";

        String name = "image";
        String originalFilename = "test.png";

        String url = "https://s3.com";

        MultipartFile file = new MockMultipartFile(name,
                originalFilename,
                "png",
                fileInputStream);
        Company company = Company.builder()
                .email("company@gmail.com")
                .password("1234abcd!")
                .companyName("test company")
                .address("test address")
                .detailAddress("test detailAddress")
                .representativeNumber("123456789")
                .build();
        doReturn(Optional.ofNullable(company))
                .when(companyRepository).findByEmail(email);
        doReturn(originalFilename)
                .when(awss3Service).originalFileName(file);
        doReturn(name)
                .when(awss3Service).filename(originalFilename);
        doReturn(url)
                .when(awss3Service).uploadFile(file);

        company.updateCompanyImg(name, url);

        String filename = companyService.updateCompanyImg(email, file);
    }

    @Test
    void getCompanyTest() {
        String email = "company@gmail.com";
        Company company = Company.builder()
                .email("company@gmail.com")
                .password("1234abcd!")
                .companyName("test company")
                .address("test address")
                .detailAddress("test detailAddress")
                .representativeNumber("123456789")
                .build();

        doReturn(Optional.ofNullable(company))
                .when(companyRepository).findByEmail(email);

        CompanyResponseDto responseDto = CompanyResponseDto.of(company);
        CompanyResponseDto result = companyService.getCompany(email);

        assertEquals(responseDto.getCompanyName(), result.getCompanyName());
    }


}
