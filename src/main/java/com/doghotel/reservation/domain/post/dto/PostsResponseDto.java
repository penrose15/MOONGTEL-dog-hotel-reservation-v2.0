package com.doghotel.reservation.domain.post.dto;

import com.doghotel.reservation.domain.room.dto.RoomResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class PostsResponseDto {
    private Long postsId;
    private String title;
    private String latitude;
    private String longitude;
    private String address;
    private String phoneNumber;
    private String checkInStartTime;
    private String checkInEndTime;
    private List<String> tags;
    private Double score;
    private List<PostsImgDto> postsImgDtos;
    private List<RoomResponseDto> roomResponseDtos;

    @Builder
    public PostsResponseDto(Long postsId, String title, String latitude, String longitude, String address, String phoneNumber, String checkInStartTime, String checkInEndTime, List<String> tags, Double score, List<PostsImgDto> postsImgDtos, List<RoomResponseDto> roomResponseDtos) {
        this.postsId = postsId;
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.checkInStartTime = checkInStartTime;
        this.checkInEndTime = checkInEndTime;
        this.tags = tags;
        this.score = score;
        this.postsImgDtos = postsImgDtos;
        this.roomResponseDtos = roomResponseDtos;
    }
}
