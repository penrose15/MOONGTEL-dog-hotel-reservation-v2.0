package com.doghotel.reservation.domain.reservation.repository;

import com.doghotel.reservation.domain.reservation.dto.ReservationDto;
import com.doghotel.reservation.domain.reservation.dto.ReservationResponseDto;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    @Query("select r " +
            "from Reservation r " +
            "where r.company.companyId = :companyId " +
            "and r.room.roomId = :roomId")
    List<Reservation> findByCompanyIdAndRoomId(Long companyId, Long roomId);

    @Query("select new com.doghotel.reservation.domain.reservation.dto.ReservationResponseDto(" +
            "r.reservationId, r.checkInDate, r.checkOutDate, r.dogCount, r.status, r.totalPrice, c.companyId, c.companyName, r1.roomId, r1.roomSize) " +
            "from Reservation r " +
            "join Company c " +
            "on r.company.companyId = c.companyId " +
            "join Room r1 " +
            "on r.room.roomId = r1.roomId " +
            "where r.customer.customerId = :customerId")
    Page<ReservationResponseDto> findByCustomerId(Long customerId, Pageable pageable);

    @Query("select new com.doghotel.reservation.domain.reservation.dto.ReservationResponseDto(" +
            "r.reservationId, r.checkInDate, r.checkOutDate, r.dogCount, r.status, r.totalPrice, c.companyId, c.companyName, r1.roomId, r1.roomSize) " +
            "from Reservation r " +
            "join Company c " +
            "on r.company.companyId = c.companyId " +
            "join Room r1 " +
            "on r.room.roomId = r1.roomId " +
            "where r.customer.customerId = :customerId " +
            "currentDate <= r.checkInDate")
    Page<ReservationResponseDto> findByCustomerIdBeforeCheckIn(Long customerId, Pageable pageable, LocalDate currentDate);

    @Query("select new com.doghotel.reservation.domain.reservation.dto.ReservationResponseDto(" +
            "r.reservationId, r.checkInDate, r.checkOutDate, r.dogCount, r.status, r.totalPrice, c.companyId, c.companyName, r1.roomId, r1.roomSize) " +
            "from Reservation r " +
            "join Company c " +
            "on r.company.companyId = c.companyId " +
            "join Room r1 " +
            "on r.room.roomId = r1.roomId " +
            "where r.customer.customerId = :customerId " +
            "currentDate > r.checkInDate")
    Page<ReservationResponseDto> findByCustomerIdAfterCheckIn(Long customerId, Pageable pageable, LocalDate currentDate);
}
