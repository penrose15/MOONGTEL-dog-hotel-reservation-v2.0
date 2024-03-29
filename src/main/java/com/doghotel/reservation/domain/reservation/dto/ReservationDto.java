package com.doghotel.reservation.domain.reservation.dto;

import com.doghotel.reservation.domain.dog.entity.Dog;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class ReservationDto {
    private String checkInDate;
    private String checkOutDate;
    private int dogCount;
    private Long roomId;
    private int totalPrice;

    private List<Long> dogList;

    @Builder
    public ReservationDto(String checkInDate, String checkOutDate, int dogCount, Long roomId, int totalPrice, List<Long> dogList) {
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.dogCount = dogCount;
        this.roomId = roomId;
        this.totalPrice = totalPrice;
        this.dogList = dogList;
    }



    public Reservation toEntity() {
        LocalDate checkIn = LocalDate.parse(this.checkInDate, DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate checkOut = LocalDate.parse(this.checkOutDate, DateTimeFormatter.ISO_LOCAL_DATE);
        return Reservation.builder()
                .checkInDate(checkIn)
                .checkOutDate(checkOut)
                .dogCount(this.dogCount)
                .totalPrice(this.totalPrice)
                .build();
    }
}
