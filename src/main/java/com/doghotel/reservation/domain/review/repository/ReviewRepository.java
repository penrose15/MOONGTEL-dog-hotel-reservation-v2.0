package com.doghotel.reservation.domain.review.repository;

import com.doghotel.reservation.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    @Query("select r from Review r where r.reservation.reservationId = :reservationId")
    Optional<Review> findByReservationId(Long reservationId);
}
