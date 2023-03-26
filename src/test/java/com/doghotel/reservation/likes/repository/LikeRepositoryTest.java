package com.doghotel.reservation.likes.repository;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.like.entity.Likes;
import com.doghotel.reservation.domain.like.repository.LikesRepository;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import com.doghotel.reservation.global.querydsl.QueryDslConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({QueryDslConfig.class})
public class LikeRepositoryTest {

    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private LikesRepository likesRepository;

    @Test
    void findLikesByPostsIdAndCustomerIdTest() {
        //given
        Posts posts = Posts.builder()
                .title("title")
                .content("content")
                .latitude("1111111")
                .longitude("2222222")
                .address("address")
                .build();
        posts = postsRepository.save(posts);
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("abcd1234!")
                .username("customer")
                .phone("010-1234-5678")
                .build();
        customer = customerRepository.save(customer);

        Likes likes = Likes.builder()
                .posts(posts)
                .customer(customer)
                .build();
        likes = likesRepository.save(likes);
        //when
        Likes response = likesRepository.findLikesByPostsIdAndCustomerId(1L, 1L)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 like"));
        //then
        assertThat(response.getLikesId())
                .isEqualTo(likes.getLikesId());
    }

    @Test
    void findLikesByCustomerIdTest() {
        //given
        Posts posts = Posts.builder()
                .title("title")
                .content("content")
                .latitude("1111111")
                .longitude("2222222")
                .address("address")
                .build();
        posts = postsRepository.save(posts);
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("abcd1234!")
                .username("customer")
                .phone("010-1234-5678")
                .build();
        customer = customerRepository.save(customer);

        Likes likes1 = Likes.builder()
                .posts(posts)
                .customer(customer)
                .build();
        Likes likes2 = Likes.builder()
                .posts(posts)
                .customer(customer)
                .build();
        likes1 = likesRepository.save(likes1);
        likes2 = likesRepository.save(likes2);

        List<Likes> likes = List.of(likes1, likes2);

        //when
        List<Likes> response = likesRepository.findLikesByCustomerId(customer.getCustomerId());

        //then
        assertThat(likes.size())
                .isEqualTo(response.size());
    }
}
