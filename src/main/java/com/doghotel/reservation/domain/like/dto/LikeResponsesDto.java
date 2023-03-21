package com.doghotel.reservation.domain.like.dto;

import com.doghotel.reservation.domain.like.entity.Likes;
import lombok.Builder;
import lombok.Getter;

@Getter
public class LikeResponsesDto {
    private Long likesId;
    private Long postsId;
    private String title;

    @Builder
    public LikeResponsesDto(Long likesId, Long postsId, String title) {
        this.likesId = likesId;
        this.postsId = postsId;
        this.title = title;
    }

    public static LikeResponsesDto of(Likes likes) {
        return LikeResponsesDto.builder()
                .likesId(likes.getLikesId())
                .postsId(likes.getPosts().getId())
                .title(likes.getPosts().getTitle())
                .build();
    }
}
