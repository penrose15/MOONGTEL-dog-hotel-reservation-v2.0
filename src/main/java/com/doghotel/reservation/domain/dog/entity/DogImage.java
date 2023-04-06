package com.doghotel.reservation.domain.dog.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DogImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dogImageId;

    @Column
    private String originalFilename;

    @Column
    private String fileName;

    @Column
    private String url;

    @ManyToOne
    @JoinColumn(name = "dogId")
    private Dog dog;


    @Builder
    public DogImage(String originalFilename, String fileName, String url, Dog dog) {
        this.originalFilename = originalFilename;
        this.fileName = fileName;
        this.url = url;
        this.dog = dog;
    }

    public DogImage addDog(Dog dog) {
        this.dog = dog;
        return this;
    }
}
