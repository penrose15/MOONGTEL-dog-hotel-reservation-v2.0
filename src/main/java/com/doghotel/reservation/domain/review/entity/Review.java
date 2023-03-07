package com.doghotel.reservation.domain.review.entity;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.post.entity.Posts;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reviewId;

    @Column(nullable = false)
    private String title;

    @Column(length = 500)
    private String content;

    @Max(5)
    @Min(1)
    @Column(nullable = false)
    private Double score;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "posts_id")
    private Posts posts;

    @Builder
    public Review(String title, String content, Double score, Customer customer, Posts posts) {
        this.title = title;
        this.content = content;
        this.score = score;
        this.customer = customer;
        this.posts = posts;
    }
}
