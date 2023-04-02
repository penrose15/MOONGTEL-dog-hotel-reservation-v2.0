package com.doghotel.reservation.domain.dog.dto;

import com.doghotel.reservation.domain.dog.entity.Dog;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DogListResponseDto {
    private Long dogId;
    private String dogName;
    private String dogImageName;
    private String dogImageUrl;

    @Builder
    private DogListResponseDto(Long dogId, String dogName, String dogImageName, String dogImageUrl) {
        this.dogId = dogId;
        this.dogName = dogName;
        this.dogImageName = dogImageName;
        this.dogImageUrl = dogImageUrl;
    }

    public static DogListResponseDto of(Dog dog) {
        return DogListResponseDto.builder()
                .dogId(dog.getDogId())
                .dogName(dog.getDogName())
                .build();
    }
}
