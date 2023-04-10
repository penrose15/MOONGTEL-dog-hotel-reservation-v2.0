package com.doghotel.reservation.global.user.email;

import org.springframework.stereotype.Component;

@Component
public interface EmailSendable {
    void send(String[] to, String subject, String message) throws InterruptedException;
}
