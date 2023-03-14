package com.doghotel.reservation.domain.room.entity;

import com.doghotel.reservation.domain.company.entity.Company;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Min;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Room {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roomId;

    @Column(nullable = false)
    private String roomSize;

    @Column(nullable = false)
    private Integer price;

    @Min(0)
    @Column
    private Integer roomCount;

    @ManyToOne
    @JoinColumn(name = "company_id")
    @Setter
    private Company company;

    @OneToMany(mappedBy = "room")
    private List<RoomImg> roomImgList = new ArrayList<>();

    @Builder
    public Room(String roomSize, Integer price, Integer roomCount, Company company) {
        this.roomSize = roomSize;
        this.price = price;
        this.roomCount = roomCount;
        this.company = company;
    }
}
