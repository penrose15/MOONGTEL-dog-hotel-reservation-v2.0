package com.doghotel.reservation.reservation.repository;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.reservation.dto.ReservationResponseDto;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.entity.ReservedDogs;
import com.doghotel.reservation.domain.reservation.entity.Status;
import com.doghotel.reservation.domain.reservation.repository.ReservationRepository;
import com.doghotel.reservation.domain.reservation.repository.ReservedDogIdRepository;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import com.doghotel.reservation.global.querydsl.QueryDslConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({QueryDslConfig.class})
public class ReservationRepositoryTest {

    @Autowired
    private ReservationRepository reservationRepository;
    @Autowired
    private ReservedDogIdRepository reservedDogIdRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    private Reservation reservation;
    private Reservation reservation1;

    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);
    }
    @AfterEach
    public void deleteAll() {
        reservationRepository.deleteAll();
        customerRepository.deleteAll();
        companyRepository.deleteAll();
        roomRepository.deleteAll();
    }

    private void setInit() {
        Customer customer = Customer.builder()
                .username("name")
                .email("customer@gmail.com")
                .password("password1234!")
                .phone("010-1111-2222")
                .build();
        customer = customerRepository.save(customer);
        Company company = Company.builder()
                .email("company@gmail.com")
                .password("1234abcd!")
                .companyName("test company")
                .address("test address")
                .detailAddress("test detailAddress")
                .representativeNumber("123456789")
                .build();
        company = companyRepository.save(company);
        Company company1 = Company.builder()
                .email("company1@gmail.com")
                .password("1234abcd!")
                .companyName("test company1")
                .address("test address")
                .detailAddress("test detailAddress")
                .representativeNumber("123456789")
                .build();
        company1 = companyRepository.save(company1);
        Room room = Room.builder()
                .price(10000)
                .roomCount(10)
                .roomSize("for small dog")
                .company(company)
                .build();
        room = roomRepository.save(room);
        Room room1 = Room.builder()
                .price(10000)
                .roomCount(10)
                .roomSize("for small dog")
                .company(company)
                .build();
        room1 = roomRepository.save(room1);

        reservation = Reservation.builder()
                .room(room1)
                .checkInDate(LocalDate.of(2024,02,03))
                .checkOutDate(LocalDate.of(2024,02,06))
                .dogCount(5)
                .totalPrice(100000)
                .customer(customer)
                .company(company)
                .build();
        reservation = reservationRepository.save(reservation);
        reservation1 = Reservation.builder()
                .room(room)
                .checkInDate(LocalDate.of(2024,02,03))
                .checkOutDate(LocalDate.of(2024,02,06))
                .dogCount(5)
                .totalPrice(100000)
                .customer(customer)
                .company(company1)
                .build();
        reservation1 = reservationRepository.save(reservation1);

        ReservedDogs reservedDogs = ReservedDogs.builder()
                .dogId(1L)
                .reservation(reservation)
                .build();
        ReservedDogs reservedDogs1 = ReservedDogs.builder()
                .dogId(1L)
                .reservation(reservation)
                .build();
        reservedDogIdRepository.save(reservedDogs);
        reservedDogIdRepository.save(reservedDogs1);
    }

    @Test
    void findByCompanyIdAndRoomIdTest() {
        //given
        setInit();

        //when
        List<Reservation> list = reservationRepository.findByCompanyIdAndRoomId(1L, 2L);


        //then
        Reservation reservation = list.get(0);
        assertThat(reservation.getRoom().getRoomId())
                .isEqualTo(2);
        assertThat(reservation.getDogCount())
                .isEqualTo(5);
        assertThat(reservation.getCompany().getCompanyName())
                .isEqualTo("test company");
    }

    @Test
    void findByCustomerIdTest() {
        setInit();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        Page<ReservationResponseDto> responseDtos = reservationRepository.findByCustomerId(1L, pageable);

        assertThat(responseDtos.getTotalElements())
                .isEqualTo(2);
        assertThat(responseDtos.getTotalPages())
                .isEqualTo(1);
    }

    @Test
    void findByCustomerIdBeforeCheckInTest() {
        setInit();
        reservation.changeStatus("ACCEPTED");
        reservation1.changeStatus("CANCELED");

        reservationRepository.save(reservation);
        reservationRepository.save(reservation1);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        Page<ReservationResponseDto> responses = reservationRepository.findByCustomerIdBeforeCheckIn(1L, pageable, LocalDate.now());

        assertThat(responses.getTotalElements())
                .isEqualTo(1);
        assertThat(responses.getTotalPages())
                .isEqualTo(1);
    }

    @Test
    void findByCustomerIdAfterCheckInTest() {
        setInit();
        reservation.changeStatus("VISITED");
        reservation1.changeStatus("VISITED");

        reservationRepository.save(reservation);
        reservationRepository.save(reservation1);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        Page<ReservationResponseDto> responses = reservationRepository.findByCustomerIdAfterCheckIn(1L, pageable, LocalDate.now());

        assertThat(responses.getTotalElements())
                .isEqualTo(0);
        assertThat(responses.getTotalPages())
                .isEqualTo(0);
    }

    @Test
    void findByCompanyIdTest() {
        setInit();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        Page<ReservationResponseDto> responses = reservationRepository.findByCompanyId(1L, pageable);

        assertThat(responses.getTotalElements())
                .isEqualTo(1);
        assertThat(responses.getTotalPages())
                .isEqualTo(1);
    }

    @Test
    void findByCompanyIdAndStatusTest() {
        setInit();
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        Page<ReservationResponseDto> responses = reservationRepository.findByCompanyIdAndStatus(1L, Status.RESERVED, pageable);

        assertThat(responses.getTotalElements())
                .isEqualTo(1);

        reservation.changeStatus("VISITED");
        reservationRepository.save(reservation);
        Page<ReservationResponseDto> responses1 = reservationRepository.findByCompanyIdAndStatus(1L, Status.VISITED, pageable);

        assertThat(responses1.getTotalElements())
                .isEqualTo(1);


    }

    @Test
    void findByReservationIdTest() {
        setInit();
        List<ReservedDogs> responses = reservedDogIdRepository.findByReservationId(1L);

        assertThat(responses.size())
                .isEqualTo(2);
    }




}
