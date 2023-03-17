package com.doghotel.reservation.domain.room.controller;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.room.dto.RoomDto;
import com.doghotel.reservation.domain.room.dto.RoomListDto;
import com.doghotel.reservation.domain.room.service.RoomService;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/room")
public class RoomController {
    private final RoomService roomService;

    @PostMapping
    public ResponseEntity<String> createRoom(@RequestBody RoomListDto dtos,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getEmail();
        String response = roomService.createRoom(dtos.getRoomDtos(), email);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
