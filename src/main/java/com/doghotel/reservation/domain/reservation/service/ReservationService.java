package com.doghotel.reservation.domain.reservation.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.dog.entity.Dog;
import com.doghotel.reservation.domain.dog.repository.DogRepository;
import com.doghotel.reservation.domain.reservation.dto.*;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.entity.ReservedDogs;
import com.doghotel.reservation.domain.reservation.entity.Status;
import com.doghotel.reservation.domain.reservation.repository.ReservationRepository;
import com.doghotel.reservation.domain.reservation.repository.ReservedDogIdRepository;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Transactional
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final CustomerRepository customerRepository;
    private final CompanyRepository companyRepository;
    private final ReservedDogIdRepository dogIdRepository;

    public ReservationCreateDto registerReservation(RegisterReservationDto dto, String email, Long postsId) {
        //validation
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException());
        Company company = companyRepository.findByPostsId(postsId)
                .orElseThrow(() -> new NoSuchElementException());

        LocalDate checkIn = LocalDate.parse(dto.getCheckInDate(), DateTimeFormatter.ISO_LOCAL_DATE);
        LocalDate checkOut = LocalDate.parse(dto.getCheckOutDate(), DateTimeFormatter.ISO_LOCAL_DATE);

        Period diff = Period.between(checkIn, checkOut);

        int days = diff.getDays();

        List<RegisterRoomInfoCountDto> registerRoomInfoCountDtos = dto.getRegisterRoomInfoCountDtos();

        List<ReservationDto> reservationDtos = new ArrayList<>();
        int totalPrice = 0;
        int totalCount = 0;

        for(int i = 0; i<registerRoomInfoCountDtos.size(); i++) {
            //예약 가능 여부 (남는 방이 있는지 확인) 체크
            RegisterRoomInfoCountDto roomInfoDto = registerRoomInfoCountDtos.get(i);
            long roomId = roomInfoDto.getRoomInfoDto().getRoomId();
            int count = roomInfoDto.getRoomCount();

            Room room = roomRepository.findById(roomId)
                    .orElseThrow(() -> new NoSuchElementException());

            List<Reservation> reservations = reservationRepository.findByCompanyIdAndRoomId(company.getCompanyId(),roomId);
            Map<LocalDate, Integer> occupied = new HashMap<>();

            for(LocalDate date = checkIn; date.isBefore(checkOut); date = date.plusDays(1L)) {
                LocalDate currentDate = date;
                int roomCount = reservations.stream()
                        .filter(reservation -> isBetweenCheckInCheckOut(reservation, currentDate))
                        .filter(reservation -> reservation.getStatus().equals(Status.ACCEPTED))
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
                .totalPrice(totalPrice * days)
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

            Reservation reservation1 = reservationRepository.save(reservation);
            List<Long> dogIdList = reservationDto.getDogList();
            List<ReservedDogs> reservedDogsList = new ArrayList<>();
            for(int i = 0; i<dogIdList.size(); i++) {
                ReservedDogs reservedDogs = ReservedDogs.builder()
                        .dogId(dogIdList.get(i))
                        .reservation(reservation1)
                        .build();
                reservedDogsList.add(reservedDogs);
            }
            dogIdRepository.saveAll(reservedDogsList);
            reservation1.setReservedDogs(reservedDogsList);

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

    public void deleteReservation(Long reservationId, String email) {
        Customer customer = customerRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException());

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new NoSuchElementException());

        if(!reservation.getCustomer().equals(customer)) {
            throw new IllegalArgumentException("본인의 예약만 삭제 가능");
        }

        if(reservation.getCheckInDate().isBefore(LocalDate.now().minusDays(1))) {
            reservation.changeStatus(Status.CANCELED.getStatus());
        }
        else {
            throw new IllegalArgumentException("예약은 체크인 하루 전 날짜에만 가능합니다.");
        }
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
