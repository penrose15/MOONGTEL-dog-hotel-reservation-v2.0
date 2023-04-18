package com.doghotel.reservation.global.health_check;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
public class ProfileController {
    private final Environment env;

    @GetMapping("/profile")
    public ResponseEntity getProfile() {
        String result =  Arrays.stream(env.getActiveProfiles())
                .findFirst()
                .orElse(null);
        return ResponseEntity.ok().body("OK");
    }

    @GetMapping("/profile/test")
    public String test() {
        return "hello";
    }
}
