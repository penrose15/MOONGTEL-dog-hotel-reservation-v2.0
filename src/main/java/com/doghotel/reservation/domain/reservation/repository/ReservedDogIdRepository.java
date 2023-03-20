package com.doghotel.reservation.domain.reservation.repository;

import com.doghotel.reservation.domain.reservation.entity.ReservedDogs;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservedDogIdRepository extends JpaRepository<ReservedDogs, Long> {
}
