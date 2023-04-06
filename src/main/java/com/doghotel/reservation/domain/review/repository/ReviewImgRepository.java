package com.doghotel.reservation.domain.review.repository;


import com.doghotel.reservation.domain.review.entity.ReviewImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewImgRepository extends JpaRepository<ReviewImg, Long> {
    @Query("select r from ReviewImg r where r.review.reviewId = :reviewId")
    List<ReviewImg> findByReviewId(Long reviewId);
}
