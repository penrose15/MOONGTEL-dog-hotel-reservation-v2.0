package com.doghotel.reservation.domain.reservation.dto;

import com.doghotel.reservation.domain.dog.dto.DogResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationCompleteDto {
    private List<DogResponseDto> reservedDogDtos;
    private String address;
    private String roomSize;
    private String checkInDate;
    private String checkOutDate;
    private Integer totalPrice;

    @Builder
    public ReservationCompleteDto(List<DogResponseDto> reservedDogDtos, String address, String roomSize, String checkInDate, String checkOutDate, Integer totalPrice) {
        this.reservedDogDtos = reservedDogDtos;
        this.address = address;
        this.roomSize = roomSize;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
    }
}
