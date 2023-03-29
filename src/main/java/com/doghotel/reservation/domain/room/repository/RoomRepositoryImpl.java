package com.doghotel.reservation.domain.room.repository;

import com.doghotel.reservation.domain.room.entity.Room;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

import static com.doghotel.reservation.domain.room.entity.QRoom.room;
import static com.querydsl.jpa.JPAExpressions.select;

@RequiredArgsConstructor
@Repository
public class RoomRepositoryImpl implements RoomRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Room> getRooms(Pageable pageable) {
        List<Room> rooms = queryFactory.select(room)
                .from(room)
                .where(room.price
                        .eq(select(room.price.min())
                                .from(room)
                                .groupBy(room.company.companyId)))
                .groupBy(room.company.companyId)
                .orderBy(room.company.posts.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPQLQuery<Room> count = queryFactory.select(room)
                .from(room)
                .where(room.price
                        .eq(select(room.price.min())
                                .from(room)
                                .groupBy(room.company.companyId)))
                .groupBy(room.company.companyId)
                .orderBy(room.company.posts.id.desc());

        return PageableExecutionUtils.getPage(rooms, pageable, count::fetchCount);
    }
}
