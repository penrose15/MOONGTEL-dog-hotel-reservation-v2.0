package com.doghotel.reservation.domain.review.entity;

import lombok.AccessLevel;
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

    @ManyToOne
    @JoinColumn(name = "review_id")
    private Review review;
}
