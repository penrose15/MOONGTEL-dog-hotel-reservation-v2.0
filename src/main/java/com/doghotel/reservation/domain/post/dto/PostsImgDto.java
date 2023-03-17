package com.doghotel.reservation.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class PostsImgDto {
    private Long postsImgId;
    private String filename;
    private String url;

    @Builder
    public PostsImgDto(Long postsImgId, String filename, String url) {
        this.postsImgId = postsImgId;
        this.filename = filename;
        this.url = url;
    }
}
