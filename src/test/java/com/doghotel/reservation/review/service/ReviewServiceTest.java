package com.doghotel.reservation.review.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.entity.ReservedDogs;
import com.doghotel.reservation.domain.reservation.repository.ReservationRepository;
import com.doghotel.reservation.domain.reservation.repository.ReservedDogIdRepository;
import com.doghotel.reservation.domain.review.entity.Review;
import com.doghotel.reservation.domain.review.repository.ReviewImgRepository;
import com.doghotel.reservation.domain.review.repository.ReviewRepository;
import com.doghotel.reservation.domain.review.repository.ReviewRepositoryImpl;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class ReviewServiceTest {
    @Autowired
    private ReviewRepository reviewRepository;
    @Autowired
    private ReviewImgRepository reviewImgRepository;
    @Autowired
    private ReviewRepositoryImpl reviewRepositoryImpl;
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


    private Customer customer;
    private Company company;
    private Reservation reservation;
    private Room room;
    private Review review;
    @BeforeEach
    public void init() {

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

        review = Review.builder()
                .title("title")
                .company(company)
                .content("content")
                .customer(customer)
                .score(0.5)
                .build();
        review = reviewRepository.save(review);
    }

    @Test
    void findByReservationIdTest() {
        Optional<Review> optional = reviewRepository.findByReservationId(reservation.getReservationId());
        assertThat(optional)
                .isPresent();
    }
}
