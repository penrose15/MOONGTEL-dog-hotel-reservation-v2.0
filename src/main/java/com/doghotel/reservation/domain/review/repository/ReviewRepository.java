package com.doghotel.reservation.domain.review.repository;

import com.doghotel.reservation.domain.review.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
