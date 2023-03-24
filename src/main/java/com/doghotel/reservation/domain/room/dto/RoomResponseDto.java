package com.doghotel.reservation.domain.room.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomResponseDto {
    private Long roomId;
    private String roomSize;
    private Integer price;

    @Builder
    public RoomResponseDto(Long roomId, String roomSize, Integer price) {
        this.roomId = roomId;
        this.roomSize = roomSize;
        this.price = price;
    }
}
