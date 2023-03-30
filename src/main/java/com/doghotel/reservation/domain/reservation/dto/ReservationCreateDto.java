package com.doghotel.reservation.domain.reservation.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class ReservationCreateDto {
    private List<ReservationDto> reservationDtos;
    private int totalCount;
    private int totalPrice;
    private String customerEmail;

    @Builder
    public ReservationCreateDto(List<ReservationDto> reservationDtos, int totalCount, int totalPrice, String customerEmail) {
        this.reservationDtos = reservationDtos;
        this.totalCount = totalCount;
        this.totalPrice = totalPrice;
        this.customerEmail = customerEmail;
    }

    public static ReservationCreateDto of(List<ReservationDto> reservationDtos, int totalCount, int totalPrice) {
        return ReservationCreateDto.builder()
                .reservationDtos(reservationDtos)
                .totalCount(totalCount)
                .totalPrice(totalPrice)
                .build();
    }

    public void addCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
