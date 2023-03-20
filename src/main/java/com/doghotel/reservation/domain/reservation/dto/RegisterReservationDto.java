package com.doghotel.reservation.domain.reservation.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class RegisterReservationDto {
    private List<RegisterRoomInfoCountDto> registerRoomInfoCountDtos;
    private Long postsId;
    private String checkInDate;
    private String checkOutDate;

}
