package com.doghotel.reservation.global.user.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class EmailMessageDto {
    private String[] to;
    private String subject;
    private String text;

    @Builder
    public EmailMessageDto(String[] to, String subject, String text) {
        this.to = to;
        this.subject = subject;
        this.text = text;
    }
}
