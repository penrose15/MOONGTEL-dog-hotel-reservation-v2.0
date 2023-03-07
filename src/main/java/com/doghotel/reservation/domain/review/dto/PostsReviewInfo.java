package com.doghotel.reservation.domain.review.dto;

import lombok.Builder;

public class PostsReviewInfo {
    private String postImg;
    private String companyName;
    private Integer totalPrice;

    @Builder
    public PostsReviewInfo(String postImg, String companyName, Integer totalPrice) {
        this.postImg = postImg;
        this.companyName = companyName;
        this.totalPrice = totalPrice;
    }
}
