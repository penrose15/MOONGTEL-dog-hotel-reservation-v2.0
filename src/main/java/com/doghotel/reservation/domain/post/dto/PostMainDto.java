package com.doghotel.reservation.domain.post.dto;

import lombok.Getter;

@Getter
public class PostMainDto {
    private String title;
    private Double score;

    public PostMainDto(String title, Double score) {
        this.title = title;
        this.score = score;
    }
}
