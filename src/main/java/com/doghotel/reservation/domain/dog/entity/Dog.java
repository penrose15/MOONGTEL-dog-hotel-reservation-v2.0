package com.doghotel.reservation.domain.dog.entity;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.dog.dto.DogUpdateDto;
import com.doghotel.reservation.domain.post.dto.PostsUpdateDto;
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
    private String dogImageName;

    @Column
    private String dogImageUrl;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    public void designateCustomer(Customer customer) {
        this.customer = customer;
    }

    public void addImage(String filename, String url) {
        this.dogImageName = filename;
        this.dogImageUrl = url;
    }

    public void updateDog(DogUpdateDto dto, String filename, String url) {
        if(dto.getDogName() != null) {
            this.dogName = dto.getDogName();
        }
        if(dto.getType() != null) {
            this.type = dto.getType();
        }
        if(dto.getGender() != null) {
            this.gender = dto.getGender();
        }
        if(dto.getAge() != null) {
            this.age = dto.getAge();
        }
        if(filename != null) {
            this.dogImageName = filename;
        }
        if(url != null) {
            this.dogImageUrl = url;
        }
    }

    @Builder
    public Dog(String dogName, String dogImageName, String dogImageUrl, String type, String gender, Integer age, Double weight, String etc, Customer customer) {
        this.dogName = dogName;
        this.dogImageName = dogImageName;
        this.dogImageUrl = dogImageUrl;
        this.type = type;
        this.gender = gender;
        this.age = age;
        this.weight = weight;
        this.etc = etc;
        this.customer = customer;
    }
}
