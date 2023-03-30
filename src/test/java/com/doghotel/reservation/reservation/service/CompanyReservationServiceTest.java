package com.doghotel.reservation.reservation.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.reservation.dto.ReservationResponseDto;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.entity.ReservedDogs;
import com.doghotel.reservation.domain.reservation.entity.Status;
import com.doghotel.reservation.domain.reservation.repository.ReservationRepository;
import com.doghotel.reservation.domain.reservation.service.CompanyReservationService;
import com.doghotel.reservation.domain.room.entity.Room;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class CompanyReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;
    @Mock
    private CompanyRepository companyRepository;
    @InjectMocks
    private CompanyReservationService companyReservationService;

    private Reservation reservation;
    private Company company;
    private String email;
    void init() {
        email = "company@gmail.com";
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("password1234!")
                .username("customer")
                .phone("010-1111-2222")
                .build();
        company = Company.builder()
                .email("company@gmail.com")
                .password("1234abcd!")
                .companyName("test company")
                .address("test address")
                .detailAddress("test detailAddress")
                .representativeNumber("123456789")
                .build();
        Room room = Room.builder()
                .roomCount(5)
                .roomSize("small")
                .price(10000)
                .company(company)
                .build();
        reservation = Reservation.builder()
                .checkInDate(LocalDate.of(2024, 02, 03))
                .checkOutDate(LocalDate.of(2024, 02, 03))
                .room(room)
                .company(company)
                .customer(customer)
                .totalPrice(20000)
                .dogCount(2)
                .build();
        reservation.changeStatus("RESERVED");
        ReservedDogs reservedDogs1 = ReservedDogs.builder()
                .dogId(1L)
                .reservation(reservation)
                .build();
        ReservedDogs reservedDogs2 = ReservedDogs.builder()
                .dogId(2L)
                .reservation(reservation)
                .build();
    }

    @Test
    void changeReservationStatus() {
        init();
        doReturn(Optional.ofNullable(reservation))
                .when(reservationRepository).findById(1L);
        doReturn(Optional.ofNullable(company))
                .when(companyRepository).findByEmail(email);
        companyReservationService.changeReservationStatus(1L, email, "ACCEPTED");
    }

    @Test
    void showReservationsTest() {
        init();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        LocalDate checkInDate = LocalDate.of(2024,02,2);
        LocalDate checkOutDate = LocalDate.of(2024,02,3);
        ReservationResponseDto reservationResponseDto1 = new ReservationResponseDto(1L, checkInDate, checkOutDate, 2, 20000, Status.ACCEPTED, 1L, "test comapany", 1L, "small");

        List<ReservationResponseDto> list = List.of(reservationResponseDto1);
        Page<ReservationResponseDto> page = new PageImpl<>(list, pageable, 1L);
        doReturn(Optional.ofNullable(company))
                .when(companyRepository).findByEmail(email);
        doReturn(page)
                .when(reservationRepository).findByCompanyId(anyLong(), any(Pageable.class));
        Page<ReservationResponseDto> response = companyReservationService.showReservations(email,0, 10);

        assertThat(response.getTotalElements())
                .isEqualTo(1);
        assertThat(response.getTotalPages())
                .isEqualTo(1);
    }

    @Test
    void showReservationsByStatus() {
        init();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        LocalDate checkInDate = LocalDate.of(2024,02,2);
        LocalDate checkOutDate = LocalDate.of(2024,02,3);


        ReservationResponseDto reservationResponseDto1 = new ReservationResponseDto(1L, checkInDate, checkOutDate, 2, 20000, Status.ACCEPTED, 1L, "test comapany", 1L, "small");

        List<ReservationResponseDto> list = List.of(reservationResponseDto1);
        Page<ReservationResponseDto> page = new PageImpl<>(list, pageable, 1L);

        doReturn(Optional.ofNullable(company))
                .when(companyRepository).findByEmail(email);
        doReturn(page)
                .when(reservationRepository).findByCompanyIdAndStatus(anyLong(), eq(Status.ACCEPTED), any(Pageable.class));

        Page<ReservationResponseDto> responses = companyReservationService.showReservationsByStatus(email, "ACCEPTED", 0, 10);

        assertThat(responses.getTotalPages())
                .isEqualTo(1);
        assertThat(responses.getTotalElements())
                .isEqualTo(1);
    }

}
