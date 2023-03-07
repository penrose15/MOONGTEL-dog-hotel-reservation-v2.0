package com.doghotel.reservation.domain.post.repository;

import com.doghotel.reservation.domain.post.entity.Posts;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostsRepository extends JpaRepository<Posts, Long> {
}
