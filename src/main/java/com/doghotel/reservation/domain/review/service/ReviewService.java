package com.doghotel.reservation.domain.review.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.service.CompanyVerifyService;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.service.CustomerVerifyingService;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.entity.PostsScore;
import com.doghotel.reservation.domain.post.repository.PostsScoreRepository;
import com.doghotel.reservation.domain.post.service.PostsFindService;
import com.doghotel.reservation.domain.post.service.PostsScoreService;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.repository.ReservationRepository;
import com.doghotel.reservation.domain.review.dto.ReviewCreateDto;
import com.doghotel.reservation.domain.review.dto.ReviewImageDto;
import com.doghotel.reservation.domain.review.dto.ReviewResponseDto;
import com.doghotel.reservation.domain.review.entity.Review;
import com.doghotel.reservation.domain.review.entity.ReviewImg;
import com.doghotel.reservation.domain.review.repository.ReviewImgRepository;
import com.doghotel.reservation.domain.review.repository.ReviewRepository;
import com.doghotel.reservation.domain.review.repository.ReviewRepositoryImpl;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import com.doghotel.reservation.global.exception.BusinessLogicException;
import com.doghotel.reservation.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static com.doghotel.reservation.global.exception.ExceptionCode.IMAGE_COUNT_LIMIT_3;
import static com.doghotel.reservation.global.exception.ExceptionCode.REVIEW_NOT_FOUND;

@Transactional
@Slf4j
@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewImgRepository reviewImgRepository;
    private final ReservationRepository reservationRepository;
    private final AWSS3Service awss3Service;
    private final CompanyVerifyService companyVerifyService;
    private final CustomerVerifyingService customerVerifyingService;
    private final ReviewRepositoryImpl reviewRepositoryImpl;
    private final PostsScoreService postsScoreService;
    public String createReview(ReviewCreateDto dto, List<ReviewImageDto> reviewImages,String email, Long reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException());

        Company company = reservation.getCompany();
        Customer customer = customerVerifyingService.findByEmail(email);

        Optional<Review> verifyReview = reviewRepository.findByReservationId(reservationId);
        if(verifyReview.isPresent()) throw new IllegalArgumentException("리뷰는 에약 한건당 하나만 달 수 있습니다.");

        Review review = dto.toEntity(company, customer);
        review.designateReservation(reservation);
        review = reviewRepository.save(review);

        List<ReviewImg> reviewImgs = new ArrayList<>();
        for (ReviewImageDto reviewImage : reviewImages) {
            ReviewImg reviewImg = reviewImage.toEntity();
            reviewImg.designateReview(review);
            reviewImgs.add(reviewImg);
        }
        reviewImgRepository.saveAll(reviewImgs);
        postsScoreService.plusScore(company.getCompanyId(), review.getScore());

        return review.getTitle();
    }



    public List<ReviewImageDto> createReviewImage(List<MultipartFile> files) throws IOException {
        List<ReviewImageDto> reviewImageDtos = new ArrayList<>();

        if(files.size() > 3) throw new BusinessLogicException(IMAGE_COUNT_LIMIT_3);

        for (MultipartFile file : files) {
            String originalFileName = awss3Service.originalFileName(file);
            String filename = awss3Service.filename(originalFileName);
            String url = awss3Service.uploadFile(file);

            ReviewImageDto review = ReviewImageDto.builder()
                    .fileName(filename)
                    .url(url)
                    .build();
            reviewImageDtos.add(review);
        }

        return reviewImageDtos;

    }

    public void deleteReview(Long reviewId) {
        Review review = findByReviewId(reviewId);
        List<ReviewImg> reviewImgs = reviewImgRepository.findByReviewId(reviewId);

        postsScoreService.minusScore(review.getCompany().getCompanyId(), review.getScore());

        reviewImgRepository.deleteAll(reviewImgs);
        reviewRepository.delete(review);
    }

    public Page<ReviewResponseDto> showReviews(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "reviewId");

        Customer customer = customerVerifyingService.findByEmail(email);

        return reviewRepositoryImpl
                .findByCustomerId(customer.getCustomerId(),
                        pageable);
    }

    public ReviewResponseDto showReview(Long reviewId, String email) {
        Review review = findByReviewId(reviewId);
        ReviewResponseDto result = reviewRepositoryImpl.findByReviewId(reviewId);
        Customer customer = customerVerifyingService.findByEmail(email);
        if(review.getCustomer().getCustomerId() != customer.getCustomerId()) {
            throw new IllegalArgumentException();
        }

        return result;
    }

    public Page<ReviewResponseDto> showReviews(Long companyId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "reviewId");

        Company company = companyVerifyService.findById(companyId);

        return reviewRepositoryImpl.findByCompanyId(companyId, pageable);

    }

    private Review findByReviewId(long reviewId) {
        return reviewRepository.findById(reviewId)
                .orElseThrow(() -> new BusinessLogicException(REVIEW_NOT_FOUND));
    }


}
