package com.doghotel.reservation.domain.reservation.controller;

import com.doghotel.reservation.domain.reservation.dto.ReservationDto;
import com.doghotel.reservation.domain.reservation.dto.ReservationResponseDto;
import com.doghotel.reservation.domain.reservation.service.CompanyReservationService;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import com.doghotel.reservation.global.response.MultiResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/company/reservation")
public class CompanyReservationController {
    private final CompanyReservationService companyReservationService;

    //예약 현황 조회
    @GetMapping("/list")
    public ResponseEntity showReservation(@RequestParam int page,
                                          @RequestParam int size,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<ReservationResponseDto> reservationPage = companyReservationService.showReservations(userDetails.getEmail(), page-1, size);
        List<ReservationResponseDto> responseDtoList = reservationPage.getContent();

        return new ResponseEntity(new MultiResponseDto<>(responseDtoList, reservationPage), HttpStatus.ACCEPTED);
    }

    @GetMapping("/list/{status}")
    public ResponseEntity showReservationByStatus(@PathVariable(name = "status") String status,
                                                   @RequestParam int page,
                                                   @RequestParam int size,
                                                   @AuthenticationPrincipal CustomUserDetails userDetails) {
        Page<ReservationResponseDto> reservationResponseDtos = companyReservationService.showReservationsByStatus(userDetails.getEmail(), status, page-1, size);
        List<ReservationResponseDto> responseDtoList = reservationResponseDtos.getContent();

        return new ResponseEntity(new MultiResponseDto<>(responseDtoList ,reservationResponseDtos), HttpStatus.ACCEPTED);
    }

    @PatchMapping("/{reservationId}")
    public ResponseEntity changeReservationStatus(@PathVariable(name = "reservationId")Long reservationId,
                                                  @RequestParam String status,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        companyReservationService.changeReservationStatus(reservationId, userDetails.getEmail(),status);

        return new ResponseEntity(HttpStatus.ACCEPTED);
    }

    @DeleteMapping("/{reservationId}")
    public ResponseEntity cancelReservationStatus(@PathVariable(name = "reservationId")Long reservationId,
                                                  @AuthenticationPrincipal CustomUserDetails userDetails) {
        companyReservationService.changeReservationStatusToCanceled(reservationId, userDetails.getEmail());
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
