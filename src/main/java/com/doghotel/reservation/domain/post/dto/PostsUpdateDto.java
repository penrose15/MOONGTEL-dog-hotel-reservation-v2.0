package com.doghotel.reservation.domain.post.dto;

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


}
