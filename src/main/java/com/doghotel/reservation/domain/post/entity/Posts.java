package com.doghotel.reservation.domain.post.entity;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.post.dto.PostsUpdateDto;
import com.doghotel.reservation.domain.tag.entity.PostsTagMap;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.validation.annotation.Validated;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@Entity
@Getter
@Validated
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Posts {

    @PrePersist
    void prePersist() {

        if(this.likeCount == null) {
            this.likeCount = 0;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 2000,nullable = false)
    private String content;

    @Column(nullable = false)
    private String latitude;

    @Column(nullable = false)
    private String longitude;

    @Column(nullable = false)
    private String address;


    private Integer likeCount;

    @Pattern(regexp = "^010-\\d{3,4}-\\d{4}$")
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

    @OneToMany(mappedBy = "posts", cascade = CascadeType.REMOVE)
    private List<PostsImg> postsImgs = new ArrayList<>();

    @OneToMany(mappedBy = "posts", cascade = CascadeType.REMOVE)
    private List<PostsTagMap> postsTagMapList = new ArrayList<>();

    @OneToOne(mappedBy = "posts", cascade = CascadeType.REMOVE)
    private PostsScore postsScore;

    @Builder
    public Posts(String title, String content, String latitude, String longitude, String address, String phoneNumber, LocalTime checkInStartTime, LocalTime checkInEndTime, Company company, List<PostsImg> postsImgs) {
        this.title = title;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.checkInStartTime = checkInStartTime;
        this.checkInEndTime = checkInEndTime;
        this.company = company;
        this.postsImgs = postsImgs;
    }

    public void designateCompany(Company company) {
        this.company = company;
    }

    public void addPostsImgs(List<PostsImg> postsImgs) {
        this.postsImgs = postsImgs;
    }

    public void updatePosts(PostsUpdateDto dto) {
        if(dto.getTitle() != null) {
            this.title = dto.getTitle();
        }
        if(dto.getContent() != null) {
            this.content = dto.getContent();
        }
        if(dto.getLatitude() != null) {
            this.latitude = dto.getLatitude();
        }
        if(dto.getLongitude() != null) {
            this.longitude = dto.getLongitude();
        }
        if(dto.getAddress() != null) {
            this.address = dto.getAddress();
        }
        if(dto.getPhoneNumber() != null) {
            this.phoneNumber = dto.getPhoneNumber();
        }
        if(dto.getCheckInEndTime() != null) {
            LocalTime startTime = LocalTime.parse(dto.getCheckInStartTime(), DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREA));
            this.checkInStartTime = startTime;
        }
        if(dto.getCheckInEndTime() != null) {
            LocalTime endTime = LocalTime.parse(dto.getCheckInEndTime(), DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREA));
            this.checkInEndTime = endTime;
        }

    }

    public void initLikesCountForTest() {
        this.likeCount = 1;
    }

    public void plusLikeCount() {
        this.likeCount += 1;
    }

    public void minusLikeCount() {
        this.likeCount -=1;
    }

}
