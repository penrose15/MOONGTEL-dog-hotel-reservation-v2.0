package com.doghotel.reservation.domain.review.dto;

import com.doghotel.reservation.domain.review.entity.ReviewImg;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewImageDto {
    private String fileName;
    private String url;

    @Builder
    public ReviewImageDto(String fileName, String url) {
        this.fileName = fileName;
        this.url = url;
    }

    public ReviewImg toEntity() {
        return ReviewImg.builder()
                .fileName(this.fileName)
                .imgUrl(this.url)
                .build();
    }
}
