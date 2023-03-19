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
    public ReservationCreateDto(List<ReservationDto> reservationDtos, int totalCount, int totalPrice) {
        this.reservationDtos = reservationDtos;
        this.totalCount = totalCount;
        this.totalPrice = totalPrice;
    }

    public void addCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }
}
