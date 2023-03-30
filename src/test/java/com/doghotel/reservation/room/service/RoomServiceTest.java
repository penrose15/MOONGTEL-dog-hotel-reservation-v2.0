package com.doghotel.reservation.room.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.room.dto.RoomDto;
import com.doghotel.reservation.domain.room.dto.RoomResponseDto;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import com.doghotel.reservation.domain.room.service.RoomService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.InstanceOfAssertFactories.PATH;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {

    @Mock
    private RoomRepository roomRepository;
    @Mock
    private CompanyRepository companyRepository;
    @InjectMocks
    private RoomService roomService;

    private String email = "company@gmail.com";
    Company company = Company.builder()
            .email("company@gmail.com")
            .password("1234abcd!")
            .companyName("test company")
            .address("test address")
            .detailAddress("test detailAddress")
            .representativeNumber("123456789")
            .build();
    RoomDto roomDto1 = RoomDto.builder()
            .roomSize("small")
            .roomCount(10)
            .price(10000)
            .build();
    RoomDto roomDto2 = RoomDto.builder()
            .roomSize("medium")
            .roomCount(5)
            .price(30000)
            .build();
    RoomDto roomDto3 = RoomDto.builder()
            .roomSize("big")
            .roomCount(1)
            .price(100000)
            .build();
    List<RoomDto> roomDtos = List.of(roomDto1, roomDto2, roomDto3);

    List<Room> roomList;

    void init() {
        roomList = new ArrayList<>();
        for (RoomDto roomDto : roomDtos) {
            Room room = roomDto.toEntity();
            room.addCompany(company);
            roomList.add(room);
        }
    }

    @Test
    void createRoomTest() {
        init();
        doReturn(Optional.ofNullable(company))
                .when(companyRepository).findByEmail(anyString());
        doReturn(roomList)
                .when(roomRepository).saveAll(anyList());
        String response = roomService.createRoom(roomDtos, email);

        assertThat(response)
                .isEqualTo("save room");
    }

    @Test
    void updateRoomTest() {
        init();
        RoomDto roomDto1 = RoomDto.builder()
                .roomSize("big")
                .roomCount(3)
                .price(70000)
                .build();
        RoomDto roomDto2 = RoomDto.builder()
                .roomSize("small")
                .roomCount(7)
                .price(30000)
                .build();

        List<RoomDto> roomDtos1 = List.of(roomDto1, roomDto2);
        List<Room> rooms = roomDtos1.stream()
                        .map(roomDto -> Room.builder().company(company)
                                .price(roomDto.getPrice())
                                .roomSize(roomDto.getRoomSize())
                                .roomCount(roomDto.getRoomCount()).build()).collect(Collectors.toList());


        doReturn(Optional.ofNullable(company))
                .when(companyRepository).findByEmail(email);
        doReturn(roomList)
                .when(roomRepository).findByCompanyId(anyLong());

        doNothing()
                .when(roomRepository).deleteAll(anyList());
        doReturn(rooms)
                .when(roomRepository).saveAll(anyList());

        roomService.updateRoom(roomDtos1, email);
    }

    @Test
    void findByCompanyIdTest() {
        init();
        doReturn(roomList)
                .when(roomRepository).findByCompanyId(anyLong());
        List<RoomResponseDto> response = roomService.findByCompanyId(anyLong());

        assertThat(response.size())
                .isEqualTo(3);
    }


}
