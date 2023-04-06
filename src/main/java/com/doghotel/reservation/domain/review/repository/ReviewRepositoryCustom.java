package com.doghotel.reservation.domain.review.repository;

import com.doghotel.reservation.domain.review.dto.ReviewResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ReviewRepositoryCustom {
    Page<ReviewResponseDto> findByCustomerId(Long customerId, Pageable pageable);

    ReviewResponseDto findByReviewId(Long reviewId);
    Page<ReviewResponseDto> findByCompanyId(Long companyId, Pageable pageable);
}
