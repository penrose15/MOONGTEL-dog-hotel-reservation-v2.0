package com.doghotel.reservation.domain.post.entity;

import com.doghotel.reservation.domain.company.entity.Company;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Posts {

    @PrePersist
    void prePersist() {
        if(this.score == null) {
            this.score = 0.0;
        }
    }

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

    private Double score;

    @Column
    private String phoneNumber;

    @Column(name = "check_in_start")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime checkInStartTime;

    @Column(name = "check_in_end")
    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime checkInEndTime;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Builder
    public Posts(String title, String content, String latitude, String longitude, String address, String phoneNumber, LocalTime checkInStartTime, LocalTime checkInEndTime, Company company) {
        this.title = title;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.checkInStartTime = checkInStartTime;
        this.checkInEndTime = checkInEndTime;
        this.company = company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }
}
