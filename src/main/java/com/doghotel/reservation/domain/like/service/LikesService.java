package com.doghotel.reservation.domain.like.service;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.service.CustomerVerifyingService;
import com.doghotel.reservation.domain.like.dto.LikeDto;
import com.doghotel.reservation.domain.like.dto.LikeResponsesDto;
import com.doghotel.reservation.domain.like.entity.Likes;
import com.doghotel.reservation.domain.like.repository.LikesRepository;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.service.PostsLikesService;
import com.doghotel.reservation.domain.post.service.PostsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class LikesService {
    private final LikesRepository likesRepository;
    private final PostsLikesService postsLikesService;
    private final CustomerVerifyingService verifyingService;

    public void changeLikes(String email, Long postsId) {
        Customer customer = verifyingService.findByEmail(email);
        Posts posts = postsLikesService.findById(postsId);

        Optional<Likes> likes = likesRepository.findLikesByPostsIdAndCustomerId(postsId, customer.getCustomerId());
        if(likes.isPresent()) {
            cancelLikes(likes.get(), posts);
        } else {
            addLikes(email, postsId);
        }
    }

    private void addLikes(String email, Long postsId) {
        Customer customer = verifyingService.findByEmail(email);
        Posts posts = postsLikesService.findById(postsId);

        LikeDto likeDto = LikeDto.builder()
                .customer(customer)
                .posts(posts)
                .build();
        likesRepository.save(likeDto.toEntity());
        posts.plusLikeCount();
    }

    private void cancelLikes(Likes likes, Posts posts) {
        posts.minusLikeCount();
        likesRepository.deleteById(likes.getLikesId());
    }

    public List<LikeResponsesDto> findMyLikes(String email) {
        Customer customer = verifyingService.findByEmail(email);
        List<Likes> likesList = likesRepository.findLikesByCustomerId(customer.getCustomerId());

        List<LikeResponsesDto> likeResponsesDtos = likesList.stream()
                .map(LikeResponsesDto::of)
                .collect(Collectors.toList());

        return likeResponsesDtos;
    }


}
