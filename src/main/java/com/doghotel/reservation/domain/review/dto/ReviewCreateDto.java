package com.doghotel.reservation.domain.review.dto;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.review.entity.Review;
import lombok.Builder;
import lombok.Getter;

@Getter
public class ReviewCreateDto {
    private String title;
    private String content;
    private Double score;

    @Builder
    public ReviewCreateDto(String title, String content, Double score) {
        this.title = title;
        this.content = content;
        this.score = score;
    }

    public Review toEntity(Company company, Customer customer) {
        return Review.builder()
                .title(this.title)
                .content(this.content)
                .score(this.score)
                .customer(customer)
                .company(company)
                .build();
    }
}
