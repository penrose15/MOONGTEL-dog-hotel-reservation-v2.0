package com.doghotel.reservation.domain.post.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor
public class PostsScore {

    @PrePersist
    void prePersist() {
        if(this.score == null) {
            score = 0.0;
        }
        if(this.totalScore == null) {
            totalScore = 0.0;
        }
        if(this.reviewCount == null) {
            reviewCount = 0;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postScoreId;

    @Column
    private Double score;

    @Column
    private Double totalScore;

    @Column
    private Integer reviewCount;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postsId")
    private Posts posts;

    public PostsScore(Posts posts) {
        this.posts = posts;
    }

    public void plusTotalScore(Double score) {
        this.totalScore += score;
    }

    public void minusTotalScore(Double score) {
        this.totalScore -= score;
    }

    public void plusReviewCount() {
        this.reviewCount += 1;
    }

    public void minusReviewCount() {
        this.reviewCount -= 1;
    }

    public void calculateScore() {
        this.score = Math.round((this.totalScore * 100.0)/this.reviewCount) / 100.0;
    }
}
