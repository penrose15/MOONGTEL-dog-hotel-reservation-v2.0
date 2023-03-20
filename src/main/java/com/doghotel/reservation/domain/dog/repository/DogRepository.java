package com.doghotel.reservation.domain.dog.repository;

import com.doghotel.reservation.domain.dog.entity.Dog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DogRepository extends JpaRepository<Dog, Long> {

    @Query("select d from Dog d where d.customer.customerId = :customerId")
    List<Dog> findByCustomerCustomerId(Long customerId);
}
