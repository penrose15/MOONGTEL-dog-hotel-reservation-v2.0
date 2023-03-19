package com.doghotel.reservation.domain.reservation.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.reservation.dto.*;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.repository.ReservationRepository;
import com.doghotel.reservation.domain.reservation.repository.ReservedRoomInfoRepository;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Transactional
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservedRoomInfoRepository reservedRoomInfoRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;

    public ReservationCreateDto registerReservation(RegisterReservationDto dto, String email, Long postsId) {
        //validation
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException());
        Company company = companyRepository.findByPostsId(postsId)
                .orElseThrow(() -> new NoSuchElementException());

        LocalDate checkIn = LocalDate.parse(dto.getCheckInDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate checkOut = LocalDate.parse(dto.getCheckOutDate(), DateTimeFormatter.ISO_LOCAL_DATE);


        Map<RegisterRoomInfoDto, Integer> reservationMap = dto.getRoomInfoDtoIntegerMap();
        List<RegisterRoomInfoDto> registerRoomInfoDtos = new ArrayList<>(reservationMap.keySet());

        List<ReservationDto> reservationDtos = new ArrayList<>();
        int totalPrice = 0;
        int totalCount = 0;

        for(int i = 0; i<registerRoomInfoDtos.size(); i++) {
            //예약 가능 여부 (남는 방이 있는지 확인) 체크
            RegisterRoomInfoDto roomInfoDto = registerRoomInfoDtos.get(i);
            long roomId = roomInfoDto.getRoomId();
            int count = reservationMap.get(roomInfoDto);

            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new NoSuchElementException());

            List<Reservation> reservations = reservationRepository.findByCompanyIdAndRoomId(company.getCompanyId(),roomId);
            Map<LocalDate, Integer> occupied = new HashMap<>();

            for(LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1L)) {
                LocalDate currentDate = date;
                int roomCount = reservations.stream()
                        .filter(reservation -> isBetweenCheckInCheckOut(reservation, currentDate))
                        .mapToInt(Reservation::getDogCount)
                        .sum();
                occupied.put(currentDate, roomCount);
            }

            List<LocalDate> dates = new ArrayList<>(occupied.keySet());
            int maxCapacity = room.getRoomCount();
            for (LocalDate date : dates) {
                int occupiedDogCount = occupied.get(date);
                if(occupiedDogCount + count >= maxCapacity) {
                    throw new IllegalArgumentException("수용가능 범위 초과");
                }
            }

            //예약한 총 방의 개수와 가격 계산
            int roomPrice = room.getPrice();
            int roomPerPrice = roomPrice * count;
            ReservationDto reservationDto = ReservationDto.builder()
                    .checkInDate(dto.getCheckInDate())
                    .checkOutDate(dto.getCheckOutDate())
                    .dogCount(count)
                    .roomId(roomId)
                    .totalPrice(roomPerPrice)
                    .build();
            reservationDtos.add(reservationDto);
            totalCount += count;
            totalPrice += roomPerPrice;
        }

        ReservationCreateDto reservationCreateDto =  ReservationCreateDto.builder()
                .reservationDtos(reservationDtos)
                .totalCount(totalCount)
                .totalPrice(totalPrice)
                .build();
        reservationCreateDto.addCustomerEmail(email);

        return reservationCreateDto;
    }

    public void createReservation(ReservationCreateDto reservationCreateDto, String email, Long postsId) {
        if (!reservationCreateDto.getCustomerEmail().equals(email)) {
            throw new IllegalArgumentException("중간에 사용자가 바뀜...?");
        }

        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException());
        Company company = companyRepository.findByPostsId(postsId)
                .orElseThrow(() -> new NoSuchElementException());

        List<ReservationDto> reservationDtos = reservationCreateDto.getReservationDtos();
        for (ReservationDto reservationDto : reservationDtos) {
            Reservation reservation = reservationDto.toEntity();
            Long roomId = reservationDto.getRoomId();
            Room room = roomRepository.findById(roomId)
                            .orElseThrow(() -> new NoSuchElementException());

            reservation.setCustomer(customer);
            reservation.setCompany(company);
            reservation.setRoom(room);

            reservationRepository.save(reservation);
        }
    }
    //전체 예약 내역 조회
    public Page<ReservationResponseDto> findReservationList(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException());
        return reservationRepository.findByCustomerId(customer.getCustomerId(), pageable);
    }

    //가기전 예약 내역
    public Page<ReservationResponseDto> findReservationBeforeCheckIn(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException());
        return reservationRepository.findByCustomerIdBeforeCheckIn(customer.getCustomerId(), pageable, LocalDate.now());
    }

    //갔다 온 예약 내역
    public Page<ReservationResponseDto> findReservationAfterCheckIn(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException());
        return reservationRepository.findByCustomerIdAfterCheckIn(customer.getCustomerId(), pageable, LocalDate.now());
    }



    public void deleteReservation() {

    }

    private boolean isBetweenCheckInCheckOut(Reservation reservation, LocalDate date) {
        LocalDate checkIn = reservation.getCheckInDate();
        LocalDate checkOut = reservation.getCheckOutDate();

        if(checkIn.isBefore(date) && checkOut.isAfter(date)) {
            return true;
        }
        return false;
    }
}
