package com.doghotel.reservation.domain.dog.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class DogPostRequestDto {
    private DogPostDto dogPostDto;
    private List<DogImageResponseDto> dogImageResponseDtos;

    public DogPostRequestDto(DogPostDto dogPostDto, List<DogImageResponseDto> dogImageResponseDtos) {
        this.dogPostDto = dogPostDto;
        this.dogImageResponseDtos = dogImageResponseDtos;
    }
}
