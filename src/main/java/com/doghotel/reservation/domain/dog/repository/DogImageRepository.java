package com.doghotel.reservation.domain.dog.repository;

import com.doghotel.reservation.domain.dog.entity.DogImage;
import com.querydsl.core.annotations.QueryEmbeddable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DogImageRepository extends JpaRepository<DogImage, Long> {

    @Query("select d " +
            "from DogImage d " +
            "where Dog.dogid = :dogId")
    List<DogImage> findByDogId(Long dogId);
}
