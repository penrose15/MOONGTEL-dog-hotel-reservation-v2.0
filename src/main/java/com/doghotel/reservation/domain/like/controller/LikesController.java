package com.doghotel.reservation.domain.like.controller;

import com.doghotel.reservation.domain.like.service.LikesService;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/like")
public class LikesController {
    private final LikesService likesService;

    @GetMapping("/{posts-id}")
    public void like(@PathVariable(name = "posts-id")Long postsId,
                     @AuthenticationPrincipal CustomUserDetails userDetails) {
        likesService.changeLikes(userDetails.getEmail(), postsId);
    }
}
