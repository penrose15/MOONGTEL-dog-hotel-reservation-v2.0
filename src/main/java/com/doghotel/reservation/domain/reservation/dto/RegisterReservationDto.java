package com.doghotel.reservation.domain.reservation.dto;

import lombok.Getter;

import java.util.Map;

@Getter
public class RegisterReservationDto {
    private Map<RegisterRoomInfoDto, Integer> roomInfoDtoIntegerMap;
    private Long postsId;
    private String checkInDate;
    private String checkOutDate;
    private int totalPrice;

    public void calculateTotalPrice(int totalPrice) {
        this.totalPrice = totalPrice;
    }
}
