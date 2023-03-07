package com.doghotel.reservation.domain.post.entity;

import com.doghotel.reservation.domain.company.entity.Company;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Posts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Lob
    @Column(nullable = false)
    private String content;

    @Column(nullable = false)
    private String latitude;

    @Column(nullable = false)
    private String longitude;

    @Column(nullable = false)
    private String address;

    @Column
    private String phoneNumber;

    @Column(name = "check_in")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime checkInTime;

    @Column(name = "check_out")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime checkOutTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

}
