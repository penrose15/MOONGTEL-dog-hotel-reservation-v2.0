package com.doghotel.reservation.global.user;

import com.doghotel.reservation.global.user.dto.AuthCodeDto;
import com.doghotel.reservation.global.user.dto.EmailDto;
import com.doghotel.reservation.global.user.dto.PasswordDto;
import com.doghotel.reservation.global.user.service.EmailService;
import com.doghotel.reservation.global.user.service.SignUpService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1/sign-up")
@RequiredArgsConstructor
public class SignUpController {
    private final SignUpService signUpService;
    private final EmailService emailService;

    @PostMapping("/email") //이메일 중복 검사
    public ResponseEntity verifyDuplicateEmail(@RequestBody @Valid EmailDto dto) {
        try {
            signUpService.verifyDuplicateEmail(dto.getEmail());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/pwd") //비밀번호 validation check
    public ResponseEntity validatePassword(@RequestBody @Valid PasswordDto dto) {
        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/auth-code")
    public ResponseEntity sendAuthCode(@RequestBody @Valid EmailDto emailDto) throws InterruptedException {
        emailService.sendAuthCode(emailDto.getEmail());

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/check-auth-code")
    public ResponseEntity verifyAuthCode(@RequestBody AuthCodeDto dto) {
        boolean verified = emailService.verifyAuthCode(dto.getEmail(), dto.getAuthCode());

        if(verified) return new ResponseEntity(HttpStatus.OK); //일치시 200
        else return new ResponseEntity(HttpStatus.BAD_REQUEST); //일치하지 않는다면 400
    }


}
