package com.doghotel.reservation.global.user.service;

import com.doghotel.reservation.global.config.redis.RedisTemplateRepository;
import com.doghotel.reservation.global.user.dto.EmailMessageDto;
import com.doghotel.reservation.global.user.email.EmailSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {
    private final RedisTemplateRepository redisRepository;
    private final EmailSender emailSender;

    public String getAuthCode() {
        char[] charset = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
        String tmpPassword = "";
        for(int i = 0; i<6; i++) {
            tmpPassword += charset[(int) (Math.random() * charset.length)];
        }
        return tmpPassword;
    }


    public void sendAuthCode(String email) throws InterruptedException {

        String authCode = getAuthCode();
        EmailMessageDto dto = AuthCodeMessage(email, authCode);
        emailSender.sendEmail(dto.getTo(), dto.getSubject(), dto.getSubject());
        saveAuthCode(email, authCode);
    }

    public EmailMessageDto AuthCodeMessage(String email, String authCode) {
        String[] to = new String[]{email};

        return EmailMessageDto.builder()
                .to(to)
                .subject("안녕하세요 "+email+" 님 ")
                .text("인증번호는"+ authCode +" 입니다")
                .build();
    }

    public void saveAuthCode(String email, String authCode) {
        redisRepository.saveAuthCode(email, authCode);
    }

    public boolean verifyAuthCode(String email, String authCode) {
        String code;
        try {
            code = redisRepository.findEmail(email);
        } catch (Exception e) {
            return false;
        }
        if(!code.equals(authCode)) {
            return false;
        }
        return true;
    }



}
