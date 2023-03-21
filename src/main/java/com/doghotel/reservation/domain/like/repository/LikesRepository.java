package com.doghotel.reservation.domain.like.repository;

import com.doghotel.reservation.domain.like.entity.Likes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikesRepository extends JpaRepository<Likes, Long> {

    @Query("select count(l) from Likes l where l.posts.id = :postsId")
    Integer likeCountByPostsId(Long postsId);

    @Query("select l from Likes l where l.posts.id = :postsId and l.customer.customerId = :customerId")
    Optional<Likes> findLikesByPostsIdAndCustomerId(Long postsId, Long customerId);

    @Query("select l from Likes l where l.customer.customerId = :customerId")
    List<Likes> findLikesByCustomerId(Long customerId);
}
