package com.doghotel.reservation.domain.reservation.controller;

import com.doghotel.reservation.domain.reservation.dto.*;
import com.doghotel.reservation.domain.reservation.service.ReservationService;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import com.doghotel.reservation.global.response.MultiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/v1/customer/reservation")
public class CustomerReservationController {

    private final ReservationService reservationService;

    @PostMapping("/{postsId}") //post 상세 페이지 -> 예약 상세 페이지로 이동
    public ResponseEntity registerReservation(@RequestBody RegisterReservationDto dto,
                                              @PathVariable(name = "postsId") Long postsId,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getEmail();
        ReservationCreateDto reservationCreateDto = reservationService.registerReservation(dto, email, postsId);

        return new ResponseEntity(reservationCreateDto, HttpStatus.CREATED);
    }

    @PostMapping("/{postsId}/details") //예약 상세 페이지에서 예약 완료
    public ResponseEntity createReservation(@RequestBody ReservationCreateDto dto,
                                            @PathVariable(name = "postsId")Long postsId,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) {
        ReservationIdDto reservationIdDto = reservationService.createReservation(dto, userDetails.getEmail(), postsId);

        return new ResponseEntity(reservationIdDto,HttpStatus.CREATED);
    }

    @GetMapping("/reservation-complete") //예약 확인 페이지
    public ResponseEntity finalReservation(@RequestParam List<Long> reservationIds,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        List<ReservationCompleteDto> reservationCompleteDtos = reservationService.reservationComplete(reservationIds, userDetails.getEmail());

        return new ResponseEntity(reservationCompleteDtos, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity findReservations(@RequestParam int page,
                                           @RequestParam int size,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<ReservationResponseDto> responseDtoPage = reservationService.findReservationList(userDetails.getEmail(), page-1, size);
        List<ReservationResponseDto> responseDtoList = responseDtoPage.getContent();

        return new ResponseEntity(new MultiResponseDto<>(responseDtoList, responseDtoPage), HttpStatus.OK);
    }

    @GetMapping("/before")
    public ResponseEntity findReservationsBeforeCheckIn(@RequestParam int page,
                                           @RequestParam int size,
                                           @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<ReservationResponseDto> responseDtoPage = reservationService.findReservationBeforeCheckIn(userDetails.getEmail(), page-1, size);
        List<ReservationResponseDto> responseDtoList = responseDtoPage.getContent();

        return new ResponseEntity(new MultiResponseDto<>(responseDtoList, responseDtoPage), HttpStatus.OK);
    }

    @GetMapping("/after")
    public ResponseEntity findReservationsAfterCheckIn(@RequestParam int page,
                                                        @RequestParam int size,
                                                        @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<ReservationResponseDto> responseDtoPage = reservationService.findReservationAfterCheckIn(userDetails.getEmail(), page-1, size);
        List<ReservationResponseDto> responseDtoList = responseDtoPage.getContent();

        return new ResponseEntity(new MultiResponseDto<>(responseDtoList, responseDtoPage), HttpStatus.OK);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity<Void> cancelReservation(@PathVariable(name = "reservationId")Long reservationId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        reservationService.deleteReservation(reservationId, userDetails.getEmail());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
