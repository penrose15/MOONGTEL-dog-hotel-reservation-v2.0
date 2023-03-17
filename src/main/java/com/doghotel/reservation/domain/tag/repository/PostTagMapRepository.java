package com.doghotel.reservation.domain.tag.repository;

import com.doghotel.reservation.domain.tag.entity.PostsTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface PostTagMapRepository extends JpaRepository<PostsTagMap, Long> {

    @Query("select p from PostsTagMap p where p.posts.id = :postsId")
    List<PostsTagMap> findByPostsPostsId(Long postsId);


}
