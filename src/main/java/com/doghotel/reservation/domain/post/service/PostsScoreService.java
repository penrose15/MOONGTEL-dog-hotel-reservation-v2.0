package com.doghotel.reservation.domain.post.service;

import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.entity.PostsScore;
import com.doghotel.reservation.domain.post.repository.PostsScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@RequiredArgsConstructor
@Service
public class PostsScoreService {
    private final PostsFindService postsFindService;
    private final PostsScoreRepository postsScoreRepository;

    public void plusScore(Long companyId, Double score) {
        Posts posts  = postsFindService.findByCompanyId(companyId);
        PostsScore postsScore = posts.getPostsScore();

        postsScore.plusTotalScore(score);
        postsScore.plusReviewCount();
//        postsScore.calculateScore();

        postsScoreRepository.save(postsScore);
    }

    public void minusScore(Long companyId, Double score) {
        Posts posts  = postsFindService.findByCompanyId(companyId);
        PostsScore postsScore = posts.getPostsScore();

        postsScore.minusTotalScore(score);
        postsScore.minusReviewCount();
//        postsScore.calculateScore();

        postsScoreRepository.save(postsScore);
    }
}
