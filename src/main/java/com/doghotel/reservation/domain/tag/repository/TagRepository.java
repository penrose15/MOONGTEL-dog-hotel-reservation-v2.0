package com.doghotel.reservation.domain.tag.repository;

import com.doghotel.reservation.domain.tag.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
}
