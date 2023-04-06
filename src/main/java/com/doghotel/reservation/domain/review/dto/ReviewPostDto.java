package com.doghotel.reservation.domain.review.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReviewPostDto {
    private ReviewCreateDto createDto;
    private List<ReviewImageDto> reviewImageDtos;

}
