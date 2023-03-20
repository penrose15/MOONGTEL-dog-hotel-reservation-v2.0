package com.doghotel.reservation.domain.customer.dto;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.review.dto.ReviewInfoDto;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class CustomerProfileViewResponseDto {
    private String username;
    private String phone;
    private String profile;
    private String profileUrl;
    private List<ReviewInfoDto> reviewList;

    @Builder
    private CustomerProfileViewResponseDto(String username, String phone, String profile, String profileUrl, List<ReviewInfoDto> reviewList) {
        this.username = username;
        this.phone = phone;
        this.profile = profile;
        this.profileUrl = profileUrl;
        this.reviewList = reviewList;
    }

    public static CustomerProfileViewResponseDto of(Customer customer, List<ReviewInfoDto> reviewList){
        return CustomerProfileViewResponseDto.builder()
                .username(customer.getUsername())
                .phone(customer.getPhone())
                .profile(customer.getProfile())
                .profileUrl(customer.getProfileUrl())
                .reviewList(reviewList)
                .build();
    }
}
