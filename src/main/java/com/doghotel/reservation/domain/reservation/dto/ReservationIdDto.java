package com.doghotel.reservation.domain.reservation.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class ReservationIdDto {
    List<Long> reservationIdList;

    public ReservationIdDto(List<Long> reservationIdList) {
        this.reservationIdList = reservationIdList;
    }
}
