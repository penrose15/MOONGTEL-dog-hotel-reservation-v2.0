package com.doghotel.reservation.domain.post.repository;

import com.doghotel.reservation.domain.post.entity.PostsImg;
import com.querydsl.core.Query;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.doghotel.reservation.domain.post.entity.QPostsImg.postsImg;
import static com.querydsl.jpa.JPAExpressions.select;

@Repository
@RequiredArgsConstructor
public class PostsImgRepositoryImpl implements PostsImgRepositoryCustom{
    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PostsImg> postsImgPage(Pageable pageable) {
        List<PostsImg> postsImgs = queryFactory
                .select(postsImg)
                .from(postsImg)
                .where(postsImg.postsImgId
                        .eq(select(postsImg.postsImgId.min())
                                .from(postsImg)
                                .groupBy(postsImg.posts.id)))
                .groupBy(postsImg.posts.id)
                .orderBy(postsImg.posts.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
        JPQLQuery<PostsImg> count = queryFactory
                .select(postsImg)
                .from(postsImg)
                .where(postsImg.postsImgId
                        .eq(select(postsImg.postsImgId.min())
                                .from(postsImg)
                                .groupBy(postsImg.posts.id)))
                .groupBy(postsImg.posts.id)
                .orderBy(postsImg.posts.id.desc());

        return PageableExecutionUtils.getPage(postsImgs, pageable, count::fetchCount);


    }
}
