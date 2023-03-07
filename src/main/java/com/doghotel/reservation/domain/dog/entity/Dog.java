package com.doghotel.reservation.domain.dog.entity;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long dogId;

    @Column
    private String dogName;

    @Column
    private String type;

    @Column
    private String gender;

    @Column
    private Integer age;

    @Column
    private Double weight;

    @Column(length = 500)
    private String etc;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "reservation_id")
    private Reservation reservation;

    @Builder
    public Dog(String dogName, String type, String gender, Integer age, Double weight, String etc, Customer customer) {
        this.dogName = dogName;
        this.type = type;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.etc = etc;
        this.customer = customer;
    }
}
