package com.doghotel.reservation.domain.post.repository;

import com.doghotel.reservation.domain.post.entity.PostsImg;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostsImgRepository extends JpaRepository<PostsImg, Long> {

    @Query("select p from PostsImg p where p.posts.id = :id")
    List<PostsImg> findByPostsId(Long id);
}
