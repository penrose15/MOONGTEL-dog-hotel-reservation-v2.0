package com.doghotel.reservation.domain.dog.dto;

import com.doghotel.reservation.domain.dog.entity.Dog;
import lombok.Builder;
import lombok.Getter;

@Getter
public class DogResponseDto {
    private Long dogId;
    private String dogName;
    private String type;
    private String gender;
    private Integer age;
    private Double weight;
    private String etc;

    @Builder
    private DogResponseDto(Long dogId, String dogName, String type, String gender, Integer age, Double weight, String etc) {
        this.dogId = dogId;
        this.dogName = dogName;
        this.type = type;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.etc = etc;
    }

    public static DogResponseDto of(Dog dog) {
        return DogResponseDto.builder()
                .dogId(dog.getDogId())
                .dogName(dog.getDogName())
                .type(dog.getType())
                .gender(dog.getGender())
                .age(dog.getAge())
                .weight(dog.getWeight())
                .etc(dog.getEtc())
                .build();
    }

}
