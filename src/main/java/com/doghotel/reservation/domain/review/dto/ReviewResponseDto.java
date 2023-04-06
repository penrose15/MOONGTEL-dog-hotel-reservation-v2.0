package com.doghotel.reservation.domain.review.dto;

import com.doghotel.reservation.domain.post.dto.ReviewImgResponseDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewResponseDto {

    private Long reviewId;
    private String title;
    private String content;
    private String score;
    private Long companyId;
    private List<ReviewImgResponseDto> reviewImgResponseDtos;

    public ReviewResponseDto(Long reviewId, String title, String content, String score, Long companyId, List<ReviewImgResponseDto> reviewImgResponseDtos) {
        this.reviewId = reviewId;
        this.title = title;
        this.content = content;
        this.score = score;
        this.companyId = companyId;
        this.reviewImgResponseDtos = reviewImgResponseDtos;
    }
}
