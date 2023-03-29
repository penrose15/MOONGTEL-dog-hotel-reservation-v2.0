package com.doghotel.reservation.domain.post.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostsResponsesDto {
    private Long id;
    private Long postsImgId;
    private String name;
    private String url;
    private String title;
    private Double score;
    private int price;

    public PostsResponsesDto(Long id,Long postsImgId, String name, String url, String title, Double score, int price) {
        this.id = id;
        this.postsImgId = postsImgId;
        this.name = name;
        this.url = url;
        this.title = title;
        this.score = score;
        this.price = price;
    }
}
