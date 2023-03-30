package com.doghotel.reservation.domain.reservation.dto;

import lombok.Getter;

@Getter
public class RegisterRoomInfoCountDto {
    private RegisterRoomInfoDto roomInfoDto;
    private Integer roomCount;

    //for test
    public RegisterRoomInfoCountDto(RegisterRoomInfoDto roomInfoDto, Integer roomCount) {
        this.roomInfoDto = roomInfoDto;
        this.roomCount = roomCount;
    }
}
