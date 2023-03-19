package com.doghotel.reservation.domain.reservation.dto;

import com.doghotel.reservation.domain.reservation.entity.Status;
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
    private int totalPrice;
    private Status status;
    private Long companyId;
    private String companyName;
    private Long roomId;
    private String roomSize;


    public ReservationResponseDto(Long reservationId, LocalDate checkInDate, LocalDate checkOutDate, int dogCount, int totalPrice,Status status, Long companyId, String companyName, Long roomId, String roomSize) {
        this.reservationId = reservationId;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.dogCount = dogCount;
        this.totalPrice = totalPrice;
        this.status = status;
        this.companyId = companyId;
        this.companyName = companyName;
        this.roomId = roomId;
        this.roomSize = roomSize;
    }
}
