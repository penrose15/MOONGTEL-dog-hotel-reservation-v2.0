package com.doghotel.reservation.domain.reservation.repository;

import com.doghotel.reservation.domain.reservation.entity.ReservedRoomInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReservedRoomInfoRepository extends JpaRepository<ReservedRoomInfo, Long> {
}
