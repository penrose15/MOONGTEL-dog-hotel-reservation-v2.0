package com.doghotel.reservation.domain.customer.entity;

import com.doghotel.reservation.domain.customer.dto.CustomerUpdateRequestDto;
import com.doghotel.reservation.domain.role.Roles;
import com.doghotel.reservation.global.user.User;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Customer extends User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long customerId;

    @Column(nullable = false, updatable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String phone;

    private String profile;

    private String profileUrl;

    @Enumerated(value = EnumType.STRING)
    private Roles roles;

    @Builder
    public Customer(String email, String password, String username, String profile, String profileUrl,String phone) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.phone = phone;
        this.profile = profile;
        this.profileUrl = profileUrl;
        this.roles = Roles.CUSTOMER;
    }

    public Customer updateCustomer(CustomerUpdateRequestDto dto) {
        if(dto.getUsername() != null) {
            this.username = dto.getUsername();
        }
        if(dto.getPhone() != null) {
            this.phone = dto.getPhone();
        }
        return this;
    }

    public void updateCustomerProfile(String url, String filename) {
        this.profileUrl = url;
        this.profile = filename;
    }
}
