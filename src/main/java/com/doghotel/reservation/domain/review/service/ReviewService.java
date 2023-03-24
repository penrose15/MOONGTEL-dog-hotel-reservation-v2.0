package com.doghotel.reservation.domain.review.service;

import com.doghotel.reservation.domain.review.repository.ReviewImgRepository;
import com.doghotel.reservation.domain.review.repository.ReviewRepository;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final AWSS3Service awss3Service;



}
