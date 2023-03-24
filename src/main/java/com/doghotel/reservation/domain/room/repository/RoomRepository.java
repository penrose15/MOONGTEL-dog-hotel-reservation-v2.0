package com.doghotel.reservation.domain.room.repository;

import com.doghotel.reservation.domain.room.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {

    @Query("select r from Room r where r.company.companyId = :companyId")
    List<Room> findByCompanyId(Long companyId);
}
