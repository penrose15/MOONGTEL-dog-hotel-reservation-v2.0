package com.doghotel.reservation.domain.room.repository;

import com.doghotel.reservation.domain.room.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface RoomRepositoryCustom {
    Page<Room> getRooms(Pageable pageable);
}
