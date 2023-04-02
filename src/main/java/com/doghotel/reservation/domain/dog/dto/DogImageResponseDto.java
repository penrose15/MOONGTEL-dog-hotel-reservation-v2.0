package com.doghotel.reservation.domain.dog.dto;


import com.doghotel.reservation.domain.dog.entity.DogImage;
import lombok.Builder;
import lombok.Getter;

import java.util.Objects;

@Getter
public class DogImageResponseDto {
    private String originalFilename;
    private String fileName;
    private String url;

    @Builder
    public DogImageResponseDto(String originalFilename, String fileName, String url) {
        this.originalFilename = originalFilename;
        this.fileName = fileName;
        this.url = url;
    }

    public DogImage toEntity() {
        return DogImage.builder()
                .originalFilename(this.originalFilename)
                .fileName(this.fileName)
                .url(this.url)
                .build();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DogImageResponseDto)) return false;
        DogImageResponseDto that = (DogImageResponseDto) o;

        return Objects.equals(getOriginalFilename(), that.getOriginalFilename()) &&
                Objects.equals(getFileName(), that.getFileName()) &&
                Objects.equals(getUrl(), that.getUrl());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getOriginalFilename(), getFileName(), getUrl());
    }
}
