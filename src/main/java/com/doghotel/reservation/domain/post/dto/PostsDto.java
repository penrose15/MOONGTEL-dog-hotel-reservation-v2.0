package com.doghotel.reservation.domain.post.dto;

import com.doghotel.reservation.domain.post.entity.Posts;
import lombok.Builder;
import lombok.Getter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Getter
public class PostsDto {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String latitude;
    @NotBlank
    private String longitude;
    @NotBlank
    private String address;
    @NotBlank
    @Pattern(regexp = "(0(2|3(1|2|3)|4(1|2|3)|5(1|2|3|4|5)|6(1|2|3|4)|10))-[^0][0-9]{2,3}-[0-9]{3,4}")
    private String phoneNumber;
    @NotBlank
    @Pattern(regexp = "(오전|오후) ([0]?[0-9]|1[0-2]):[0-5][0-9]")
    private String checkInStartTime;
    @NotBlank
    @Pattern(regexp = "(오전|오후) ([0]?[0-9]|1[0-2]):[0-5][0-9]")
    private String checkInEndTime;

    private List<String> tagList;

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
