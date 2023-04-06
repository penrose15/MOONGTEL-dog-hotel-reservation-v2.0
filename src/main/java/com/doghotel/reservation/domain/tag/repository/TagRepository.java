package com.doghotel.reservation.domain.tag.repository;

import com.doghotel.reservation.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {

    @Query("select t.title from Tag t " +
            "join fetch PostsTagMap pt " +
            "on t.id = pt.tag.id " +
            "where pt.posts.id = :postsId")
    List<String> findTagsByPostsId(Long postsId);
}
