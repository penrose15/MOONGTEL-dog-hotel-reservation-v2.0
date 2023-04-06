package com.doghotel.reservation.domain.review.repository;

import com.doghotel.reservation.domain.review.dto.ReviewImageDto;
import com.doghotel.reservation.domain.review.dto.ReviewResponseDto;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

import static com.doghotel.reservation.domain.review.entity.QReview.review;
import static com.doghotel.reservation.domain.review.entity.QReviewImg.reviewImg;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewRepositoryCustom{
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<ReviewResponseDto> findByCustomerId(Long customerId, Pageable pageable) {
        List<ReviewResponseDto> reviews = queryFactory
                .select(Projections.constructor(ReviewResponseDto.class,
                        review.reviewId,
                        review.title,
                        review.content,
                        review.score,
                        review.company.companyId,
                        Projections.list(
                                Projections.constructor(ReviewImageDto.class, reviewImg.fileName, reviewImg.imgUrl)
                        )))
                .from(review)
                .join(reviewImg)
                .fetchJoin()
                .on(review.reviewId.eq(reviewImg.review.reviewId))
                .where(review.customer.customerId.eq(customerId))
                .groupBy(review.reviewId)
                .orderBy(review.reviewId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPQLQuery<ReviewResponseDto> count = queryFactory
                .select(Projections.constructor(ReviewResponseDto.class,
                        review.reviewId,
                        review.title,
                        review.content,
                        review.score,
                        review.company.companyId,
                        Projections.list(
                                Projections.constructor(ReviewImageDto.class, reviewImg.fileName, reviewImg.imgUrl)
                        )))
                .from(review)
                .join(reviewImg)
                .fetchJoin()
                .on(review.reviewId.eq(reviewImg.review.reviewId))
                .where(review.customer.customerId.eq(customerId))
                .groupBy(review.reviewId);
        return PageableExecutionUtils.getPage(reviews,pageable, count::fetchCount);


    }

    @Override
    public ReviewResponseDto findByReviewId(Long reviewId) {
        return queryFactory
                .select(Projections.constructor(ReviewResponseDto.class,
                        review.reviewId,
                        review.title,
                        review.content,
                        review.score,
                        review.company.companyId,
                        Projections.list(
                                Projections.constructor(ReviewImageDto.class, reviewImg.fileName, reviewImg.imgUrl)
                        )))
                .from(review)
                .join(reviewImg)
                .fetchJoin()
                .on(review.reviewId.eq(reviewImg.review.reviewId))
                .where(review.reviewId.eq(reviewId))
                .fetchOne();
    }

    @Override
    public Page<ReviewResponseDto> findByCompanyId(Long companyId, Pageable pageable) {
        List<ReviewResponseDto> reviews = queryFactory
                .select(Projections.constructor(ReviewResponseDto.class,
                        review.reviewId,
                        review.title,
                        review.content,
                        review.score,
                        review.company.companyId,
                        Projections.list(
                                Projections.constructor(ReviewImageDto.class, reviewImg.fileName, reviewImg.imgUrl)
                        )))
                .from(review)
                .join(reviewImg)
                .fetchJoin()
                .on(review.reviewId.eq(reviewImg.review.reviewId))
                .where(review.company.companyId.eq(companyId))
                .groupBy(review.reviewId)
                .orderBy(review.reviewId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPQLQuery<ReviewResponseDto> count = queryFactory
                .select(Projections.constructor(ReviewResponseDto.class,
                        review.reviewId,
                        review.title,
                        review.content,
                        review.score,
                        review.company.companyId,
                        Projections.list(
                                Projections.constructor(ReviewImageDto.class, reviewImg.fileName, reviewImg.imgUrl)
                        )))
                .from(review)
                .join(reviewImg)
                .fetchJoin()
                .on(review.reviewId.eq(reviewImg.review.reviewId))
                .where(review.company.companyId.eq(companyId))
                .groupBy(review.reviewId);
        return PageableExecutionUtils.getPage(reviews,pageable, count::fetchCount);
    }
}
