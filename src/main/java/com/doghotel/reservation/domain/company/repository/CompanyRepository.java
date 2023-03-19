package com.doghotel.reservation.domain.company.repository;

import com.doghotel.reservation.domain.company.entity.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    Optional<Company> findByEmail(String email);
    @Query("select c from Company c where c.Posts.id = :postsId")
    Optional<Company> findByPostsId(Long postsId);
}
