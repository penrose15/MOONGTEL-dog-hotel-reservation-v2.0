package com.doghotel.reservation.domain.dog.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DogPostRequestDto {
    private DogPostDto dogPostDto;
    private List<DogImageResponseDto> dogImageResponseDtos;
}
