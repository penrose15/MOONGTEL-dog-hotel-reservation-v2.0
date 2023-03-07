package com.doghotel.reservation.domain.like.entity;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.post.entity.Posts;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long likesId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "posts_id")
    private Posts posts;

    @Builder
    public Likes(Customer customer, Posts posts) {
        this.customer = customer;
        this.posts = posts;
    }
}
