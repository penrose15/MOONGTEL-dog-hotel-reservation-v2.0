package com.doghotel.reservation.domain.room.repository;

import com.doghotel.reservation.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoomRepository extends JpaRepository<Room, Long> {
}
