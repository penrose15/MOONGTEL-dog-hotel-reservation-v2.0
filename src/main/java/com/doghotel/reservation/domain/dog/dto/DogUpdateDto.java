package com.doghotel.reservation.domain.dog.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DogUpdateDto {
    private String dogName;
    private String type;
    private String gender;
    private Integer age;
    private Double weight;
    private String etc;

    @Builder
    public DogUpdateDto(String dogName, String type, String gender, Integer age, Double weight, String etc) {
        this.dogName = dogName;
        this.type = type;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.etc = etc;
    }
}
