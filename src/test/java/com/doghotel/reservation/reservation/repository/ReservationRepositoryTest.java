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

    private Customer customer;
    private Company company;
    private Reservation reservation;
    private Room room;

    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);

        customer = Customer.builder()
                .username("name")
                .email("customer@gmail.com")
                .password("password1234!")
                .phone("010-1111-2222")
                .build();
        customer = customerRepository.save(customer);
        company = Company.builder()
                .email("company@gmail.com")
                .password("1234abcd!")
                .companyName("test company")
                .address("test address")
                .detailAddress("test detailAddress")
                .representativeNumber("123456789")
                .build();
        company = companyRepository.save(company);

        room = Room.builder()
                .price(10000)
                .roomCount(10)
                .roomSize("for small dog")
                .company(company)
                .build();
        room = roomRepository.save(room);

        reservation = Reservation.builder()
                .room(room)
                .checkInDate(LocalDate.of(2024,02,03))
                .checkOutDate(LocalDate.of(2024,02,06))
                .dogCount(5)
                .totalPrice(100000)
                .customer(customer)
                .company(company)
                .build();
        reservation = reservationRepository.save(reservation);

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
    @AfterEach
    public void deleteAll() {
        reservationRepository.deleteAll();
        customerRepository.deleteAll();
        companyRepository.deleteAll();
        roomRepository.deleteAll();
    }

    @Test
    void findByCompanyIdAndRoomIdTest() {
        //given

        //when
        List<Reservation> list = reservationRepository.findByCompanyIdAndRoomId(company.getCompanyId(), room.getRoomId());


        //then
        Reservation reservation = list.get(0);
        assertThat(reservation.getRoom().getRoomSize())
                .isEqualTo("for small dog");
        assertThat(reservation.getDogCount())
                .isEqualTo(5);
        assertThat(reservation.getCompany().getCompanyName())
                .isEqualTo("test company");
    }

    @Test
    void findByCustomerIdTest() {

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        Page<ReservationResponseDto> responseDtos = reservationRepository.findByCustomerId(customer.getCustomerId(), pageable);

        assertThat(responseDtos.getTotalElements())
                .isEqualTo(1);
        assertThat(responseDtos.getTotalPages())
                .isEqualTo(1);
    }

    @Test
    void findByCustomerIdBeforeCheckInTest() {

        reservation.changeStatus("ACCEPTED");

        reservationRepository.save(reservation);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        Page<ReservationResponseDto> responses = reservationRepository.findByCustomerIdBeforeCheckIn(customer.getCustomerId(), pageable, LocalDate.now());

        assertThat(responses.getTotalElements())
                .isEqualTo(1);
    }

    @Test
    void findByCustomerIdAfterCheckInTest() {

        reservation.changeStatus("VISITED");

        reservationRepository.save(reservation);

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        Page<ReservationResponseDto> responses = reservationRepository.findByCustomerIdAfterCheckIn(customer.getCustomerId(), pageable, LocalDate.now());

        assertThat(responses.getTotalElements())
                .isEqualTo(0);
    }

    @Test
    void findByCompanyIdTest() {

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        Page<ReservationResponseDto> responses = reservationRepository.findByCompanyId(company.getCompanyId(), pageable);

        assertThat(responses.getTotalElements())
                .isEqualTo(1);
        assertThat(responses.getTotalPages())
                .isEqualTo(1);
    }

    @Test
    void findByCompanyIdAndStatusTest() {

        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "reservationId");
        Page<ReservationResponseDto> responses = reservationRepository.findByCompanyIdAndStatus(company.getCompanyId(), Status.RESERVED, pageable);

        assertThat(responses.getTotalElements())
                .isEqualTo(1);

        reservation.changeStatus("VISITED");
        reservationRepository.save(reservation);
        Page<ReservationResponseDto> responses1 = reservationRepository.findByCompanyIdAndStatus(company.getCompanyId(), Status.VISITED, pageable);

        assertThat(responses1.getTotalElements())
                .isEqualTo(1);


    }

    @Test
    void findByReservationIdTest() {


        List<ReservedDogs> responses = reservedDogIdRepository.findByReservationId(reservation.getReservationId());

        assertThat(responses.size())
                .isEqualTo(2);
    }




}
