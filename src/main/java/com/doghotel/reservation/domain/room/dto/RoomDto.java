package com.doghotel.reservation.domain.room.dto;

import com.doghotel.reservation.domain.room.entity.Room;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RoomDto {
    private String roomSize;
    private Integer price;
    private Integer roomCount;

    @Builder
    public RoomDto(String roomSize, Integer price, Integer roomCount) {
        this.roomSize = roomSize;
        this.price = price;
        this.roomCount = roomCount;
    }

    public Room toEntity() {
        return Room.builder()
                .roomSize(this.roomSize)
                .roomCount(this.roomCount)
                .price(this.price)
                .build();
    }
}
