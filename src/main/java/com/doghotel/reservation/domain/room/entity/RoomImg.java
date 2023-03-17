package com.doghotel.reservation.domain.room.entity;

import lombok.Builder;
import lombok.Getter;

import javax.persistence.*;

@Getter
@Entity
public class RoomImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long roomImgId;

    private String name;

    @Column
    private String url;

    @ManyToOne
    @JoinColumn(name ="room_id")
    private Room room;

    @Builder
    public RoomImg(String name, String url, Room room) {
        this.name = name;
        this.url = url;
        this.room = room;
    }
}
