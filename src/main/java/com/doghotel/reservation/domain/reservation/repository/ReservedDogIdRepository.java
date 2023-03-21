package com.doghotel.reservation.domain.reservation.repository;

import com.doghotel.reservation.domain.reservation.entity.ReservedDogs;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReservedDogIdRepository extends JpaRepository<ReservedDogs, Long> {

    @Query("select r from ReservedDogs r where r.reservation.reservationId = :reservationId")
    List<ReservedDogs> findByReservationId(Long reservationId);
}
