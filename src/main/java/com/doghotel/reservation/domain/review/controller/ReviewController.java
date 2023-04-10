package com.doghotel.reservation.domain.review.controller;

import com.doghotel.reservation.domain.review.dto.ReviewImageDto;
import com.doghotel.reservation.domain.review.dto.ReviewPostDto;
import com.doghotel.reservation.domain.review.dto.ReviewResponseDto;
import com.doghotel.reservation.domain.review.service.ReviewService;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import com.doghotel.reservation.global.response.MultiResponseDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/review")
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping("/images")
    public ResponseEntity createReviewImageDto(@RequestPart List<MultipartFile> files) throws IOException {
        List<ReviewImageDto>  response = reviewService.createReviewImage(files);
        return new ResponseEntity(response, HttpStatus.CREATED);
    }

    @PostMapping("/{reservation-id}")
    public ResponseEntity createReview(@RequestBody ReviewPostDto dto,
                                       @PathVariable(value = "reservation-id") Long reservationId,
                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        reviewService.createReview(dto.getCreateDto(), dto.getReviewImageDtos(), userDetails.getEmail(), reservationId);

        return new ResponseEntity(HttpStatus.CREATED);
    }

    @DeleteMapping("/{review-id}")
    public ResponseEntity deleteReview(@PathVariable(name = "review-id") Long reviewId) {
        reviewService.deleteReview(reviewId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity showReviews(@RequestParam int page,
                                      @RequestParam int size,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<ReviewResponseDto> response = reviewService.showReviews(userDetails.getEmail(), page-1,size);
        List<ReviewResponseDto> responseList = response.getContent();

        return new ResponseEntity(new MultiResponseDto<>(responseList,response), HttpStatus.OK);
    }

    @GetMapping("/{review-id}")
    public ResponseEntity showReview(@PathVariable(name = "review-id") Long reviewId
            ,@AuthenticationPrincipal CustomUserDetails userDetails) {
        ReviewResponseDto response = reviewService.showReview(reviewId, userDetails.getEmail());
        return new ResponseEntity(response, HttpStatus.OK);
    }

    @GetMapping("/company/{company-id}")
    public ResponseEntity showReviews(@PathVariable(name = "company-id")Long companyId,
                                      @RequestParam int page,
                                      @RequestParam int size) {
        Page<ReviewResponseDto> reviewResponsesPage = reviewService.showReviews(companyId, page, size);
        List<ReviewResponseDto> reviewResponsesList = reviewResponsesPage.getContent();

        return new ResponseEntity(new MultiResponseDto<>(reviewResponsesList, reviewResponsesPage), HttpStatus.OK);
    }


}
