package com.doghotel.reservation.domain.reservation.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.service.CustomerVerifyingService;
import com.doghotel.reservation.domain.dog.dto.DogResponseDto;
import com.doghotel.reservation.domain.dog.service.DogService;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import com.doghotel.reservation.domain.reservation.dto.*;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.entity.ReservedDogs;
import com.doghotel.reservation.domain.reservation.entity.Status;
import com.doghotel.reservation.domain.reservation.repository.ReservationRepository;
import com.doghotel.reservation.domain.reservation.repository.ReservedDogIdRepository;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import com.doghotel.reservation.global.exception.BusinessLogicException;
import com.doghotel.reservation.global.exception.ExceptionCode;
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
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class ReservationService {
    private final ReservationRepository reservationRepository;
    private final PostsRepository postsRepository;
    private final RoomRepository roomRepository;
    private final CustomerVerifyingService customerVerifyingService;
    private final CompanyRepository companyRepository;
    private final ReservedDogIdRepository dogIdRepository;
    private final DogService dogService;

    public ReservationCreateDto registerReservation(RegisterReservationDto dto, String email, Long postsId) {
        //validation
        Customer customer = customerVerifyingService.findByEmail(email);
        Posts posts = postsRepository.findById(postsId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));
        Company company = posts.getCompany();

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

            Map<LocalDate, Integer> occupied = getReservationInfo(reservations, checkIn, checkOut);

            isReservationAvailable(room, occupied, count);

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

        ReservationCreateDto reservationCreateDto =  ReservationCreateDto.of(reservationDtos, totalCount, totalPrice * days);
        reservationCreateDto.addCustomerEmail(email);

        return reservationCreateDto;
    }

    private Map<LocalDate, Integer> getReservationInfo(List<Reservation> reservations, LocalDate checkIn, LocalDate checkOut) {
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
        return occupied;
    }

    private void isReservationAvailable(Room room, Map<LocalDate, Integer> occupied, int count) {
        List<LocalDate> dates = new ArrayList<>(occupied.keySet());
        int maxCapacity = room.getRoomCount();
        for (LocalDate date : dates) {
            int occupiedDogCount = occupied.get(date);
            if(occupiedDogCount + count >= maxCapacity) {
                throw new IllegalArgumentException("수용가능 범위 초과");
            }
        }
    }

    public ReservationIdDto createReservation(ReservationCreateDto reservationCreateDto, String email, Long postsId) {
        if (!reservationCreateDto.getCustomerEmail().equals(email)) {
            throw new BusinessLogicException(ExceptionCode.CUSTOMER_NOT_MATCH);
        }

        Customer customer = customerVerifyingService.findByEmail(email);
        Posts posts = postsRepository.findById(postsId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.POST_NOT_FOUND));
        Company company = posts.getCompany();

        List<Long> reservationIdList = new ArrayList<>();
        List<ReservationDto> reservationDtos = reservationCreateDto.getReservationDtos();
        for (ReservationDto reservationDto : reservationDtos) {
            Reservation reservation = reservationDto.toEntity();
            Long roomId = reservationDto.getRoomId();
            Room room = roomRepository.findById(roomId)
                            .orElseThrow(() -> new BusinessLogicException(ExceptionCode.ROOM_NOT_FOUND));

            reservation.designateCustomer(customer);
            reservation.designateCompany(company);
            reservation.designateRoom(room);

            reservation = reservationRepository.save(reservation);
            List<Long> dogIdList = reservationDto.getDogList();
            if(dogIdList.size() > reservationCreateDto.getTotalCount()) {
                throw new IllegalArgumentException("선택한 방의 개수만큼만 보낼 수 있습니다.");
            }
            List<ReservedDogs> reservedDogsList = new ArrayList<>();
            for(int i = 0; i<dogIdList.size(); i++) {
                ReservedDogs reservedDogs = ReservedDogs.builder()
                        .dogId(dogIdList.get(i))
                        .reservation(reservation)
                        .build();
                reservedDogsList.add(reservedDogs);
            }
            dogIdRepository.saveAll(reservedDogsList);
            reservation.addReservedDogs(reservedDogsList);
            reservationIdList.add(reservation.getReservationId());
        }

        return new ReservationIdDto(reservationIdList);
    }
    public List<ReservationCompleteDto> reservationComplete(List<Long> reservationIdList, String email) {
        List<ReservationCompleteDto> reservationCompleteDtos = new ArrayList<>();

        for (Long aLong : reservationIdList) {
            Reservation reservation = reservationRepository.findById(aLong)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 예약"));
            List<ReservedDogs> reservedDogs = dogIdRepository.findByReservationId(reservation.getReservationId());
            List<Long> reservedDogIds = reservedDogs.stream()
                    .map(ReservedDogs::getDogId)
                    .collect(Collectors.toList());
            List<DogResponseDto> dogResponseDtos = new ArrayList<>();
            for (Long dogId : reservedDogIds) {
                DogResponseDto dogResponseDto = dogService.showDogByDogId(dogId, email);
                dogResponseDtos.add(dogResponseDto);
            }
            String checkInDate = reservation.getCheckInDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            String checkOutDate = reservation.getCheckInDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
            ReservationCompleteDto reservationCompleteDto = ReservationCompleteDto.builder()
                    .reservedDogDtos(dogResponseDtos)
                    .address(reservation.getCompany().getPosts().getAddress())
                    .roomSize(reservation.getRoom().getRoomSize())
                    .checkInDate(checkInDate)
                    .checkOutDate(checkOutDate)
                    .totalPrice(reservation.getTotalPrice())
                    .build();
            reservationCompleteDtos.add(reservationCompleteDto);
        }
        return reservationCompleteDtos;

    }

    //전체 예약 내역 조회
    public Page<ReservationResponseDto> findReservationList(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Customer customer = customerVerifyingService.findByEmail(email);
        return reservationRepository.findByCustomerId(customer.getCustomerId(), pageable);
    }

    //가기전 예약 내역
    public Page<ReservationResponseDto> findReservationBeforeCheckIn(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Customer customer = customerVerifyingService.findByEmail(email);
        return reservationRepository.findByCustomerIdBeforeCheckIn(customer.getCustomerId(), pageable, LocalDate.now());
    }

    //갔다 온 예약 내역
    public Page<ReservationResponseDto> findReservationAfterCheckIn(String email, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Customer customer = customerVerifyingService.findByEmail(email);
        return reservationRepository.findByCustomerIdAfterCheckIn(customer.getCustomerId(), pageable, LocalDate.now());
    }

    public void deleteReservation(Long reservationId, String email) {
        Customer customer = customerVerifyingService.findByEmail(email);

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
