package com.doghotel.reservation.domain.post.repository;

import com.doghotel.reservation.domain.post.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostsRepository extends JpaRepository<Posts, Long> {

    @Query("select p from Posts p where p.company.companyId = :companyId")
    Optional<Posts> findPostsByCompanyId(Long companyId);
}
