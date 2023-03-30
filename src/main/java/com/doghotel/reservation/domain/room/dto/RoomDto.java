package com.doghotel.reservation.domain.room.dto;

import com.doghotel.reservation.domain.room.entity.Room;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if(!(o instanceof RoomDto)) return false;
        RoomDto roomDto = (RoomDto) o;
        return Objects.equals(getRoomSize(), roomDto.getRoomSize()) &&
                Objects.equals(getPrice(), roomDto.getPrice()) &&
                Objects.equals(getRoomCount(), roomDto.getRoomCount());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getRoomSize(), getPrice(), getRoomCount());
    }

}
