package com.doghotel.reservation.domain.review.dto;

import com.doghotel.reservation.domain.review.entity.ReviewImg;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ReviewInfoDto {
    //동주님 청문회 열어야겠네요

    private String createdAt;
    private String modifiedAt;
    private Long id;
    private String title;
    private String content;
    private Double score;
    private Long userId;
    private PostsReviewInfo companyInfo;
    private List<ReviewImgDto> reviewImg;

    @Builder
    public ReviewInfoDto(String createdAt, String modifiedAt, Long id, String title, String content, Double score, Long userId, PostsReviewInfo companyInfo, List<ReviewImgDto> reviewImg) {
        this.createdAt = createdAt;
        this.modifiedAt = modifiedAt;
        this.id = id;
        this.title = title;
        this.content = content;
        this.score = score;
        this.userId = userId;
        this.companyInfo = companyInfo;
        this.reviewImg = reviewImg;
    }
}
