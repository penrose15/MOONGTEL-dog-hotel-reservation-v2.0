package com.doghotel.reservation.global.user;

import com.doghotel.reservation.global.user.dto.AuthCodeDto;
import com.doghotel.reservation.global.user.dto.ChangePasswordDto;
import com.doghotel.reservation.global.user.dto.EmailDto;
import com.doghotel.reservation.global.user.service.EmailService;
import com.doghotel.reservation.global.user.service.PasswordService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/password")
public class PasswordController {
    private final EmailService emailService;
    private final PasswordService passwordService;

    @PostMapping("/auth-code")
    public ResponseEntity sendAuthCode(@RequestBody @Valid EmailDto emailDto) throws InterruptedException {
        emailService.sendAuthCode(emailDto.getEmail());

        return new ResponseEntity(HttpStatus.OK);
    }

    @PostMapping("/check-auth-code")
    public ResponseEntity verifyAuthCode(@RequestBody AuthCodeDto dto) {
        boolean verified = emailService.verifyAuthCode(dto.getEmail(), dto.getAuthCode());

        if(verified) return new ResponseEntity(dto.getEmail(),HttpStatus.OK); //일치시 200
        else return new ResponseEntity(HttpStatus.BAD_REQUEST); //일치하지 않는다면 400
    }

    @PatchMapping //비밀번호 변경
    public ResponseEntity changePassword(@RequestBody ChangePasswordDto dto) {
        passwordService.changePassword(dto.getEmail(), dto.getPassword());

        return new ResponseEntity(HttpStatus.OK);
    }

}
