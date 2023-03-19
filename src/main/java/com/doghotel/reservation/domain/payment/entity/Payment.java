package com.doghotel.reservation.domain.payment.entity;

import lombok.Getter;

import javax.persistence.*;

@Entity
@Getter
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Integer money;

    private String payBank;
    private String payApproval;
    private String payKind;
}
