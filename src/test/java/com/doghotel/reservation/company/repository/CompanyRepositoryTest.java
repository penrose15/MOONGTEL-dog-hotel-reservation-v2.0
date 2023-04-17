package com.doghotel.reservation.company.repository;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.post.dto.PostsResponseDto;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import com.doghotel.reservation.global.exception.BusinessLogicException;
import com.doghotel.reservation.global.exception.ExceptionCode;
import com.doghotel.reservation.global.querydsl.QueryDslConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CompanyRepositoryTest {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private PostsRepository postsRepository;

    @BeforeEach
    public void setUp() {
        Company company = Company.builder()
                .email("company@naver.com")
                .password("password12!")
                .companyName("hello")
                .address("서울특별시 강남구 강남대로 1번지")
                .detailAddress(" ")
                .representativeNumber("123456789")
                .build();
        companyRepository.save(company);
    }

    @AfterEach
    public void clean() {
        companyRepository.deleteAll();
    }

    @Test
    @Transactional
    void findByEmail() {
        Optional<Company> company1 = companyRepository.findByEmail("company@naver.com");

        assertThat(company1)
                .isPresent();

        Company c = company1
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.COMPANY_NOT_FOUND));

        assertThat(c.getCompanyName())
                .isEqualTo("hello");
    }
}
