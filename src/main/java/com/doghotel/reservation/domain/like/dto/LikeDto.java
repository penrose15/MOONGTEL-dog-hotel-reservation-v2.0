package com.doghotel.reservation.domain.like.dto;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.like.entity.Likes;
import com.doghotel.reservation.domain.post.entity.Posts;
import lombok.Builder;

public class LikeDto {
    private Posts posts;
    private Customer customer;

    @Builder
    public LikeDto(Posts posts, Customer customer) {
        this.posts = posts;
        this.customer = customer;
    }

    public Likes toEntity() {
        return Likes.builder()
                .customer(this.customer)
                .posts(this.posts)
                .build();
    }
}
