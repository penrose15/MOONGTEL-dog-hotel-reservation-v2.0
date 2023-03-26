package com.doghotel.reservation.domain.dog.dto;

import com.doghotel.reservation.domain.dog.entity.Dog;
import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Validated
public class DogPostDto {


    @NotBlank
    @Pattern(regexp = "^[a-zA-Zㄱ-힣0-9|s]*$")
    private String dogName;
    @NotBlank
    private String type;
    @NotBlank
    private String gender;
    @NotBlank
    @Min(0)
    private Integer age;
    @NotBlank
    @Min(0)
    private Double weight;
    private String etc;

    @Builder//for test
    public DogPostDto(String dogName, String type, String gender, Integer age, Double weight, String etc) {
        this.dogName = dogName;
        this.type = type;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.etc = etc;
    }

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
