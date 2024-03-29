package com.doghotel.reservation.domain.post.dto;

import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.room.dto.RoomResponseDto;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Getter
public class PostsResponseDto {
    private Long postsId;
    private Long companyId;
    private String title;
    private String content;
    private String latitude;
    private String longitude;
    private String address;
    private String phoneNumber;
    private String checkInStartTime;
    private String checkInEndTime;
    private List<String> tags;
    private List<PostsImgDto> postsImgDtos;
    private List<RoomResponseDto> roomResponseDtos;

    @Builder
    public PostsResponseDto(Long postsId, Long companyId, String title, String content, String latitude, String longitude, String address, String phoneNumber, String checkInStartTime, String checkInEndTime, List<String> tags,  List<PostsImgDto> postsImgDtos, List<RoomResponseDto> roomResponseDtos) {
        this.postsId = postsId;
        this.companyId = companyId;
        this.title = title;
        this.content = content;
        this.latitude = latitude;
        this.longitude = longitude;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.checkInStartTime = checkInStartTime;
        this.checkInEndTime = checkInEndTime;
        this.tags = tags;
        this.postsImgDtos = postsImgDtos;
        this.roomResponseDtos = roomResponseDtos;
    }



    public static PostsResponseDto of(Posts posts, List<PostsImgDto> postsImgDtos,List<String> tags, List<RoomResponseDto> roomDtos) {
        return PostsResponseDto.builder()
                .postsId(posts.getId())
                .companyId(posts.getCompany().getCompanyId())
                .title(posts.getTitle())
                .content(posts.getContent())
                .latitude(posts.getLatitude())
                .longitude(posts.getLongitude())
                .address(posts.getAddress())
                .phoneNumber(posts.getPhoneNumber())
                .checkInStartTime(posts.getCheckInStartTime().format(DateTimeFormatter.ofPattern("a hh:mm")))
                .checkInEndTime(posts.getCheckInEndTime().format(DateTimeFormatter.ofPattern("a hh:mm")))
                .tags(tags)
                .postsImgDtos(postsImgDtos)
                .roomResponseDtos(roomDtos)
                .build();
    }
}
