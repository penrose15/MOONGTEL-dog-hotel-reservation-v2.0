package com.doghotel.reservation.domain.post.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.post.dto.PostsDto;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Transactional
@Service
@RequiredArgsConstructor
public class PostsService {
    private final PostsRepository postsRepository;
    private final CompanyRepository companyRepository;

    public String createPosts(String email, PostsDto dto) {
        Company company = verifyingEmail(email);

        Posts posts = dto.toEntity();
        posts.setCompany(company);

        posts = postsRepository.save(posts);
        return posts.getTitle();
    }





    private Company verifyingEmail(String email) {
        return companyRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회사"));
    }
}
