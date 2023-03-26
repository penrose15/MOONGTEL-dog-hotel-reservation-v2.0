package com.doghotel.reservation.likes.service;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.service.CustomerVerifyingService;
import com.doghotel.reservation.domain.like.dto.LikeDto;
import com.doghotel.reservation.domain.like.entity.Likes;
import com.doghotel.reservation.domain.like.repository.LikesRepository;
import com.doghotel.reservation.domain.like.service.LikesService;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.service.PostsFindService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class LikesServiceTest {

    @Mock
    private LikesRepository likesRepository;
    @Mock
    private PostsFindService postsFindService;
    @Mock
    private CustomerVerifyingService customerVerifyingService;
    @InjectMocks
    private LikesService likesService;

    @Test
    void changeLikesTestIfLikeIsExist() {
        //given
        String email = "customer@gmail.com";
        Long postsId = 1L;
        Long customerId = 1L;
        Posts posts = Posts.builder()
                .title("title")
                .content("content")
                .latitude("1111111")
                .longitude("2222222")
                .address("address")
                .build();
        posts.initLikesCountForTest();
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("abcd1234!")
                .username("customer")
                .phone("010-1234-5678")
                .build();
        Likes likes1 = Likes.builder()
                .posts(posts)
                .customer(customer)
                .build();
        likesRepository.save(likes1);
        //when
        doReturn(customer)
                .when(customerVerifyingService).findByEmail(email);
        doReturn(posts)
                .when(postsFindService).findById(postsId);
        doReturn(Optional.of(likes1))
                .when(likesRepository).findLikesByPostsIdAndCustomerId(anyLong(), anyLong());
        doNothing()
                .when(likesRepository).deleteById(anyLong());

        likesService.changeLikes(email, postsId);

        //then
        assertThat(posts.getLikeCount())
                .isEqualTo(0);
        assertThat(0)
                .isEqualTo(likesRepository.findAll().size());
    }

    @Test
    void changeLikesIfNotExist() {
        //given
        String email = "customer@gmail.com";
        Long postsId = 1L;
        Long customerId = 1L;
        Posts posts = Posts.builder()
                .title("title")
                .content("content")
                .latitude("1111111")
                .longitude("2222222")
                .address("address")
                .build();
        posts.initLikesCountForTest();
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("abcd1234!")
                .username("customer")
                .phone("010-1234-5678")
                .build();
        //when
        doReturn(customer)
                .when(customerVerifyingService).findByEmail(email);
        doReturn(posts)
                .when(postsFindService).findById(postsId);
        doReturn(Optional.empty())
                .when(likesRepository).findLikesByPostsIdAndCustomerId(anyLong(), anyLong());
        LikeDto likeDto = LikeDto.builder()
                .customer(customer)
                .posts(posts)
                .build();
        Likes like = likeDto.toEntity();
        doReturn(like)
                .when(likesRepository).save(any(Likes.class));
        likesService.changeLikes(email, postsId);

        assertThat(posts.getLikeCount())
                .isEqualTo(2);
    }
}
