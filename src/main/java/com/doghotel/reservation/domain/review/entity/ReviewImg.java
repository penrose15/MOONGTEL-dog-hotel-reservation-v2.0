package com.doghotel.reservation.domain.review.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImg {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long reviewImgId;

    private String fileName;

    private String imgUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @Builder
    public ReviewImg(String fileName, String imgUrl, Review review) {
        this.fileName = fileName;
        this.imgUrl = imgUrl;
        this.review = review;
    }


    public void designateReview(Review review) {
        this.review = review;
    }
}
