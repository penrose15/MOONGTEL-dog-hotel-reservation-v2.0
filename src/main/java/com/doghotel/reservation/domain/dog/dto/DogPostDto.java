package com.doghotel.reservation.domain.dog.dto;

import com.doghotel.reservation.domain.dog.entity.Dog;
import lombok.Getter;

@Getter
public class DogPostDto {

    private String dogName;
    private String type;
    private String gender;
    private Integer age;
    private Double weight;
    private String etc;

    public Dog toEntity() {
        return Dog.builder()
                .dogName(this.dogName)
                .type(this.type)
                .gender(this.gender)
                .age(this.age)
                .weight(this.weight)
                .etc(this.etc)
                .build();
    }

}
