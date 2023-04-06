package com.doghotel.reservation.domain.reservation.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.reservation.dto.ReservationResponseDto;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.entity.Status;
import com.doghotel.reservation.domain.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional
public class CompanyReservationService {
    private final ReservationRepository reservationRepository;
    private final CompanyRepository companyRepository;

    //예약 상태 변경
    public void changeReservationStatus(Long reservationId, String email, String status) {
        Reservation reservation = findReservation(reservationId);
        if(!reservation.getCompany().equals(verifyCompany(email))) {
            throw new IllegalArgumentException("다른 회사의 예약은 못건든다.");
        }
        reservation.changeStatus(status);
        log.info("reservation status changed : " + reservation.getStatus().getStatus());
    }

    //삭제..?
    public void changeReservationStatusToCanceled(Long reservationId, String email) {
        Reservation reservation = findReservation(reservationId);
        if(!reservation.getCompany().equals(verifyCompany(email))) {
            throw new IllegalArgumentException("다른 회사의 예약은 못건든다.");
        }
        reservation.changeStatus("CANCELED");
    }

    //회사에 예약된 내역들 전체 조회
    public Page<ReservationResponseDto> showReservations(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "reservationId");
        Company company = verifyCompany(email);
        return reservationRepository.findByCompanyId(company.getCompanyId(), pageable);
    }

    //상태별로 조회
    public Page<ReservationResponseDto> showReservationsByStatus(String email, String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "reservationId");
        Company company = verifyCompany(email);
        Status status1 = Status.convertToStatus(status);

        return reservationRepository.findByCompanyIdAndStatus(company.getCompanyId(), status1, pageable);
    }


    private Company verifyCompany(String email) {
        return companyRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException());
    }
    private Reservation findReservation(Long reservationId) {
        return reservationRepository.findById(reservationId)
                .orElseThrow(() -> new  NoSuchElementException());
    }
}
