package com.doghotel.reservation.domain.reservation.entity;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReservedDogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reservedDogsId;

    @Column
    private Long dogId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Builder
    public ReservedDogs(Long dogId, Reservation reservation) {
        this.dogId = dogId;
        this.reservation = reservation;
    }
}
