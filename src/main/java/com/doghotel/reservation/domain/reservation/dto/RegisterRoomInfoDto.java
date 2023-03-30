package com.doghotel.reservation.domain.reservation.dto;

import lombok.Getter;

@Getter
public class RegisterRoomInfoDto {
    private Long roomId;
    private String roomSize;

    //for test
    public RegisterRoomInfoDto(Long roomId, String roomSize) {
        this.roomId = roomId;
        this.roomSize = roomSize;
    }
}
