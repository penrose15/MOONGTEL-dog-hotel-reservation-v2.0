package com.doghotel.reservation.reservation.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.service.CustomerVerifyingService;
import com.doghotel.reservation.domain.dog.service.DogService;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.reservation.dto.*;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.entity.ReservedDogs;
import com.doghotel.reservation.domain.reservation.entity.Status;
import com.doghotel.reservation.domain.reservation.repository.ReservationRepository;
import com.doghotel.reservation.domain.reservation.repository.ReservedDogIdRepository;
import com.doghotel.reservation.domain.reservation.service.ReservationService;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class ReservationServiceTest {
    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private CustomerVerifyingService customerVerifyingService;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private ReservedDogIdRepository reservedDogIdRepository;

    @Mock
    private DogService dogService;

    @InjectMocks
    private ReservationService reservationService;

    private RegisterRoomInfoDto roomInfoDto;
    private RegisterRoomInfoCountDto registerRoomInfoCountDto;
    private RegisterReservationDto registerReservationDto;
    private String email;
    private Long postsId;
    private Customer customer;
    private Company company;
    private Room room;
    private Reservation reservation1;
    private Reservation reservation2;
    private List<Reservation> reservationList;
    private ReservationDto reservationDto;
    private List<ReservationDto> reservationDtos;
    private ReservationCreateDto reservationCreateDto;

    @Autowired
    private Gson gson;

    void init() {
        roomInfoDto = new RegisterRoomInfoDto(1L, "small");
        registerRoomInfoCountDto = new RegisterRoomInfoCountDto(roomInfoDto, 2);
        registerReservationDto = new RegisterReservationDto(List.of(registerRoomInfoCountDto), 1L, "2024-02-15", "2024-02-19");

        email = "customer@gmail.com";
        postsId = 1L;
        customer = Customer.builder()
                .email("customer@gmail.com")
                .password("password1235!")
                .phone("010-1111-2222")
                .username("test customer")
                .build();

        company = Company.builder()
                .address("address")
                .detailAddress("detailAddress")
                .representativeNumber("123456789")
                .companyName("test company")
                .email("company@gmail.com")
                .password("password1234!")
                .build();

        Posts posts = Posts.builder()
                .score(0.0)
                .title("title")
                .content("content")
                .longitude("1111111")
                .latitude("111111111")
                .company(company)
                .address("address")
                .build();
        company.addPosts(posts);
        room = Room.builder()
                .price(10000)
                .roomSize("small")
                .roomCount(10)
                .company(company)
                .build();
        reservation1 = Reservation.builder()
                .checkInDate(LocalDate.of(2024,2,15))
                .checkOutDate(LocalDate.of(2024,2,18))
                .dogCount(1)
                .totalPrice(40000)
                .room(room)
                .customer(customer)
                .company(company)
                .build();
        reservation1.changeStatus("ACCEPTED");
        reservation2 = Reservation.builder()
                .checkInDate(LocalDate.of(2024,2,16))
                .checkOutDate(LocalDate.of(2024,2,19))
                .dogCount(1)
                .totalPrice(40000)
                .room(room)
                .customer(customer)
                .company(company)
                .build();
        reservation2.changeStatus("ACCEPTED");
        reservationList = List.of(reservation1, reservation2);
        reservationDto = ReservationDto.builder()
                .checkInDate("2024-02-15")
                .checkOutDate("2024-02-19")
                .dogCount(1)
                .roomId(1L)
                .totalPrice(50000)
                .dogList(List.of(1L))
                .build();
        reservationDtos = List.of(reservationDto);
        reservationCreateDto = ReservationCreateDto.builder()
                .reservationDtos(reservationDtos)
                .totalCount(1)
                .totalPrice(50000)
                .customerEmail("customer@gmail.com")
                .build();
    }

    @Test
    void registerReservation() {
        //given(...)
        init();

        //when
        doReturn(customer)
                .when(customerVerifyingService).findByEmail(email);
        doReturn(Optional.of(company))
                .when(companyRepository).findByPostsId(postsId);
        doReturn(Optional.ofNullable(room))
                .when(roomRepository).findById(anyLong());
        doReturn(reservationList)
                .when(reservationRepository).findByCompanyIdAndRoomId(anyLong(), anyLong());

        //then
        ReservationCreateDto response = reservationService.registerReservation(registerReservationDto, email, 1L);

        assertThat(response.getTotalPrice())
                .isEqualTo(80000);
        assertThat(response.getReservationDtos().size())
                .isEqualTo(1);
    }

    @Test
    void registerReservationThrowException() {
        init();
        //given
        room.changeRoomCountForTest(2);

        //when
        doReturn(customer)
                .when(customerVerifyingService).findByEmail(email);
        doReturn(Optional.of(company))
                .when(companyRepository).findByPostsId(postsId);
        doReturn(Optional.ofNullable(room))
                .when(roomRepository).findById(anyLong());
        doReturn(reservationList)
                .when(reservationRepository).findByCompanyIdAndRoomId(anyLong(), anyLong());

        //then
        boolean result = true;
        try {
            reservationService.registerReservation(registerReservationDto, email, 1L);
        } catch (IllegalArgumentException e) {
            result  = false;
        }

        assertThat(result)
                .isFalse();
    }

    @Test
    void createReservation() {
        init();
        doReturn(customer)
                .when(customerVerifyingService).findByEmail(email);
        doReturn(Optional.ofNullable(company))
                .when(companyRepository).findByPostsId(postsId);
        doReturn(Optional.ofNullable(room))
                .when(roomRepository).findById(anyLong());
        Reservation reservation = reservationDto.toEntity();
        reservation.designateCompany(company);
        reservation.designateCustomer(customer);
        reservation.designateRoom(room);
        doReturn(reservation)
                .when(reservationRepository).save(any(Reservation.class));
        ReservedDogs reservedDogs = ReservedDogs.builder()
                .dogId(1L)
                .reservation(reservation)
                .build();
        List<ReservedDogs> reservedDogsList = List.of(reservedDogs);
        doReturn(reservedDogsList)
                .when(reservedDogIdRepository).saveAll(anyList());

        ReservationIdDto reservationIdDto = reservationService.createReservation(reservationCreateDto, email, postsId);

        assertThat(reservationIdDto.getReservationIdList().size())
                .isEqualTo(1);
    }

    @Test
    void reservationCompleteTest() {
        init();
        Reservation reservation = Reservation.builder()
                .checkInDate(LocalDate.of(2024,02,15))
                .checkOutDate(LocalDate.of(2024,02,19))
                .totalPrice(40000)
                .dogCount(1)
                .company(company)
                .customer(customer)
                .room(room)
                .build();
        doReturn(Optional.ofNullable(reservation))
                .when(reservationRepository).findById(anyLong());
        ReservedDogs reservedDogs = ReservedDogs.builder()
                .dogId(1L)
                .reservation(reservation)
                .build();
        List<ReservedDogs> reservedDogsList = List.of(reservedDogs);
        doReturn(reservedDogsList)
                .when(reservedDogIdRepository).findByReservationId(anyLong());
        List<Long> reservedIdList = List.of(1L);
        ReservationIdDto reservationIdDto = new ReservationIdDto(reservedIdList);

        List<ReservationCompleteDto> reservationCompleteDtos = reservationService.reservationComplete(reservationIdDto, email);
        assertThat(reservationCompleteDtos.size())
                .isEqualTo(1);
    }

    @Test
    void findReservationListTest() {
        init();
        Pageable pageable = PageRequest.of(0, 10);
        doReturn(customer)
                .when(customerVerifyingService).findByEmail(email);

        ReservationResponseDto responseDto1 = new ReservationResponseDto(
                1L,
                LocalDate.of(2024,02,15),
                LocalDate.of(2024,02,18),
                1,
                40000,
                Status.RESERVED,
                1L,
                "test company",
                1L,
                "room");
        ReservationResponseDto responseDto2 = new ReservationResponseDto(
                2L,
                LocalDate.of(2024,02,16),
                LocalDate.of(2024,02,19),
                1,
                40000,
                Status.RESERVED,
                1L,
                "test company",
                1L,
                "room");
        List<ReservationResponseDto> responseDtoList = List.of(responseDto1, responseDto2);
        Page<ReservationResponseDto> responseDtoPage = new PageImpl<>(responseDtoList, pageable, 2);
        doReturn(responseDtoPage)
                .when(reservationRepository).findByCustomerId(anyLong(), eq(pageable));
        Page<ReservationResponseDto> response = reservationService.findReservationList(email, 0, 10);

        assertThat(response.getTotalElements())
                .isEqualTo(2);
        assertThat(response.getTotalPages())
                .isEqualTo(1);
    }
}
