package com.doghotel.reservation.domain.post.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostsUpdateDto {
    private String title;
    private String content;
    private String latitude;
    private String longitude;
    private String address;
    private String phoneNumber;
    private String checkInStartTime;
    private String checkInEndTime;
    private List<String> tagList;

    @Builder //for Test
    public PostsUpdateDto(String title, String content, String latitude, String longitude, String address, String phoneNumber, String checkInStartTime, String checkInEndTime, List<String> tagList) {
        this.title = title;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.checkInStartTime = checkInStartTime;
        this.checkInEndTime = checkInEndTime;
        this.tagList = tagList;
    }
}
