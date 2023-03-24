package com.doghotel.reservation.company.repository;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.post.dto.PostsResponseDto;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import com.doghotel.reservation.global.querydsl.QueryDslConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({QueryDslConfig.class})
public class CompanyRepositoryTest {
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private PostsRepository postsRepository;

    @Test
    void findByPostsId() {
        Company company = Company.builder()
                .representativeNumber("123456789")
                .address("address")
                .detailAddress("detailAddress")
                .companyName("company1")
                .password("password123")
                .email("abc@gmail.com")
                .build();
        company = companyRepository.save(company);

        Posts posts = Posts.builder()
                .title("title")
                .content("content")
                .longitude("1111")
                .latitude("11111")
                .address("address")
                .company(company)
                .build();
        postsRepository.save(posts);

        Company result = companyRepository.findByPostsId(posts.getId())
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회사"));

        assertThat(company.getCompanyId())
                .isEqualTo(result.getCompanyId());
    }
}
