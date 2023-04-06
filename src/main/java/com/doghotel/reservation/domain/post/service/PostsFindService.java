package com.doghotel.reservation.domain.post.service;


import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class PostsFindService {
    private final PostsRepository postsRepository;

    public Posts findById(Long postsId) {
        return postsRepository.findById(postsId)
                .orElseThrow(() -> new NoSuchElementException("존재핞는 홍보글"));
    }

    public Posts findByCompanyId(Long companyId) {
        return postsRepository.findPostsByCompanyId(companyId)
                .orElseThrow(() -> new NoSuchElementException());
    }
}
