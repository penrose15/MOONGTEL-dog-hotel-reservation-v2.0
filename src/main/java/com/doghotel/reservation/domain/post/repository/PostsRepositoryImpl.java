package com.doghotel.reservation.domain.post.repository;

import com.doghotel.reservation.domain.post.dto.PostsResponsesDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.sql.SQLException;
import java.util.List;

import static com.doghotel.reservation.domain.post.entity.QPosts.posts;
import static com.doghotel.reservation.domain.post.entity.QPostsImg.postsImg;
import static com.doghotel.reservation.domain.room.entity.QRoom.room;
import static com.querydsl.jpa.JPAExpressions.select;

@Repository
@RequiredArgsConstructor
public class PostsRepositoryImpl implements PostsRepositoryCustom{
    private final JPAQueryFactory queryFactory;


    @Override
    public Page<PostsResponsesDto> getMainPages(Pageable pageable) {
        List<PostsResponsesDto> mainPages = queryFactory
                .select(Projections.constructor(PostsResponsesDto.class,posts.id,postsImg.postsImgId, postsImg.name, postsImg.url, posts.title, posts.score, room.price))
                .from(posts)
                .join(postsImg)
                .on(posts.id.eq(postsImg.posts.id))
                .join(room)
                .on(posts.company.companyId.eq(room.company.companyId))
                .where(postsImg.postsImgId.in(select(postsImg.postsImgId.min())
                        .from(posts)
                        .join(postsImg)
                        .on(postsImg.posts.id.eq(posts.id))
                                .groupBy(postsImg.posts.id)
                        ).and(room.price.in(select(room.price.min())
                        .from(room)
                        .join(posts.company)
                        .on(room.company.companyId.eq(posts.company.companyId))
                        .groupBy(posts.company.posts.id)
                )))
                .groupBy(posts.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<PostsResponsesDto> count = queryFactory
                .select(Projections.constructor(PostsResponsesDto.class,posts.id,postsImg.postsImgId, postsImg.name, postsImg.url, posts.title, posts.score, room.price))
                .from(posts)
                .join(postsImg)
                .on(posts.id.eq(postsImg.posts.id))
                .join(room)
                .on(posts.company.companyId.eq(room.company.companyId))
                .where(postsImg.postsImgId.in(select(postsImg.postsImgId.min())
                        .from(posts)
                        .join(postsImg)
                        .on(postsImg.posts.id.eq(posts.id))

                ).and(room.price.in(select(room.price.min())
                        .from(room)
                        .join(posts.company)
                        .on(room.company.companyId.eq(posts.company.companyId))
                        .groupBy(posts.company.posts.id)
                )))
                .groupBy(posts.id);

        return PageableExecutionUtils.getPage(mainPages, pageable, count::fetchCount);
    }

    @Override
    public Page<PostsResponsesDto> searchPagesByTitleOrContentOrAddress(String keyword, Pageable pageable) {
        List<PostsResponsesDto> mainPages = queryFactory
                .select(Projections.constructor(PostsResponsesDto.class,posts.id,postsImg.postsImgId, postsImg.name, postsImg.url, posts.title, posts.score, room.price))
                .from(posts)
                .join(postsImg)
                .on(posts.id.eq(postsImg.posts.id))
                .join(room)
                .on(posts.company.companyId.eq(room.company.companyId))
                .where(postsImg.postsImgId.in(select(postsImg.postsImgId.min())
                        .from(posts)
                        .join(postsImg)
                        .on(postsImg.posts.id.eq(posts.id))
                        .groupBy(postsImg.posts.id)
                ).and(room.price.in(select(room.price.min())
                                .from(room)
                                .join(posts.company)
                                .on(room.company.companyId.eq(posts.company.companyId))
                                .groupBy(posts.company.posts.id)
                        ))
                        .and(isTitleContains(keyword).or(isContentsContains(keyword).or(isAddressContains(keyword)))))
                .groupBy(posts.id)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<PostsResponsesDto> count = queryFactory
                .select(Projections.constructor(PostsResponsesDto.class,posts.id,postsImg.postsImgId, postsImg.name, postsImg.url, posts.title, posts.score, room.price))
                .from(posts)
                .join(postsImg)
                .on(posts.id.eq(postsImg.posts.id))
                .join(room)
                .on(posts.company.companyId.eq(room.company.companyId))
                .where(postsImg.postsImgId.in(select(postsImg.postsImgId.min())
                                .from(posts)
                                .join(postsImg)
                                .on(postsImg.posts.id.eq(posts.id))
                                .groupBy(postsImg.posts.id)
                        ).and(room.price.in(select(room.price.min())
                                .from(room)
                                .join(posts.company)
                                .on(room.company.companyId.eq(posts.company.companyId))
                                .groupBy(posts.company.posts.id)
                        ))
                        .and(isTitleContains(keyword).or(isContentsContains(keyword).or(isAddressContains(keyword)))))
                .groupBy(posts.id);

        return PageableExecutionUtils.getPage(mainPages, pageable, count::fetchCount);
    }




    private BooleanExpression isTitleContains(String title) {
        return title != null ? posts.title.contains(title) : null;
    }

    private BooleanExpression isContentsContains(String contents) {
        return contents != null? posts.content.contains(contents) : null;
    }

    private BooleanExpression isAddressContains(String address) {
        return address != null? posts.address.contains(address) : null;
    }
}
