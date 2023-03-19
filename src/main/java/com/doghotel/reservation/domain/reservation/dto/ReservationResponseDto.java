package com.doghotel.reservation.domain.reservation.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservationResponseDto {
    private Long reservationId;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private int dogCount;
    private boolean accept;
    private int totalPrice;
    private Long companyId;
    private String companyName;
    private Long roomId;
    private String roomSize;


    public ReservationResponseDto(Long reservationId, LocalDate checkInDate, LocalDate checkOutDate, int dogCount, boolean accept, int totalPrice, Long companyId, String companyName, Long roomId, String roomSize) {
        this.reservationId = reservationId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.dogCount = dogCount;
        this.accept = accept;
        this.totalPrice = totalPrice;
        this.companyId = companyId;
        this.companyName = companyName;
        this.roomId = roomId;
        this.roomSize = roomSize;
    }
}
