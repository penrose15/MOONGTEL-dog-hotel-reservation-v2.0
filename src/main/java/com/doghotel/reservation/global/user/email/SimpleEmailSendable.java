package com.doghotel.reservation.global.user.email;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

@RequiredArgsConstructor
@Slf4j
public class SimpleEmailSendable implements EmailSendable{

    private final JavaMailSender javaMailSender;


    @Override
    public void send(String[] to, String subject, String message) throws InterruptedException {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(to);
        mailMessage.setText(message);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);
        javaMailSender.send(mailMessage);

        log.info("send message");
    }
}
