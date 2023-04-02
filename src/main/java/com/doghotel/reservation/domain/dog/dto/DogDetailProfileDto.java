package com.doghotel.reservation.domain.dog.dto;

import com.doghotel.reservation.domain.dog.entity.DogImage;
import lombok.Getter;

import java.util.List;

@Getter
public class DogDetailProfileDto {
    private DogResponseDto dogResponseDto;
    private List<DogImageResponseDto> dogImageResponseDtoList;

    public DogDetailProfileDto(DogResponseDto dogResponseDto, List<DogImageResponseDto> dogImageResponseDtoList) {
        this.dogResponseDto = dogResponseDto;
        this.dogImageResponseDtoList = dogImageResponseDtoList;
    }
}
