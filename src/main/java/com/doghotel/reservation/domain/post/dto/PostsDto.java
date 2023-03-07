package com.doghotel.reservation.domain.post.dto;

import com.doghotel.reservation.domain.post.entity.Posts;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Getter
public class PostsDto {
    private String title;
    private String content;
    private String latitude;
    private String longitude;
    private String address;
    private String phoneNumber;
    private String checkInStartTime;
    private String checkInEndTime;

    public Posts toEntity() {
        LocalTime startTime = LocalTime.parse(this.checkInStartTime, DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREA));
        LocalTime endTime = LocalTime.parse(this.checkInEndTime, DateTimeFormatter.ofPattern("a hh:mm").withLocale(Locale.KOREA));


        return Posts.builder()
                .title(this.title)
                .content(this.content)
                .latitude(this.latitude)
                .longitude(this.longitude)
                .address(this.address)
                .checkInStartTime(startTime)
                .checkInEndTime(endTime)
                .build();
    }
}
