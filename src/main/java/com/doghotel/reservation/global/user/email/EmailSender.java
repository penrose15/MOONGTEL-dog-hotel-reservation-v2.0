package com.doghotel.reservation.global.user.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailSendException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class EmailSender {
    private final EmailSendable emailSendable;

    public void sendEmail(String[] to, String subject, String message) throws InterruptedException {
        try {
            emailSendable.send(to, subject, message);
        } catch (MailSendException e) {
            e.printStackTrace();

        }

    }
}
