package com.doghotel.reservation.domain.room.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.room.dto.RoomDto;
import com.doghotel.reservation.domain.room.dto.RoomResponseDto;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final CompanyRepository companyRepository;

    public String createRoom(List<RoomDto> roomDtos, String email) {
        Company company = verifyCompany(email);
        List<Room> roomList = new ArrayList<>();
        for(int i = 0; i<roomDtos.size(); i++) {
            Room room = roomDtos.get(i).toEntity();
            room.addCompany(company);
            roomList.add(room);
        }
        roomRepository.saveAll(roomList);
        return "save room";
    }

    public void updateRoom(List<RoomDto> roomDtos, String email) {
        Company company = verifyCompany(email);

        List<Room> roomList = roomRepository.findByCompanyId(company.getCompanyId());

        List<Room> deleteList = new ArrayList<>();
        List<Room> insertList = new ArrayList<>();

        for (Room room : roomList) {
            RoomDto roomDto = RoomDto.builder()
                    .roomCount(room.getRoomCount())
                    .price(room.getPrice())
                    .roomSize(room.getRoomSize())
                    .build();
            boolean found = false;
            for (RoomDto dto : roomDtos) {
                if(dto.equals(roomDto)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                deleteList.add(room);
            }
        }

        for (RoomDto requestRoom : roomDtos) {
            boolean found = false;
            for (Room room : roomList) {
                RoomDto roomDto = RoomDto.builder()
                        .roomCount(room.getRoomCount())
                        .price(room.getPrice())
                        .roomSize(room.getRoomSize())
                        .build();
                if(roomDto.equals(requestRoom)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                insertList.add(Room.builder()
                                .roomSize(requestRoom.getRoomSize())
                                .roomCount(requestRoom.getRoomCount())
                                .price(requestRoom.getPrice())
                                .company(company)
                                .build());
            }
        }
        System.out.println(deleteList.size());
        System.out.println(insertList.size());

        roomRepository.deleteAll(deleteList);
        roomRepository.saveAll(insertList);

    }

    public List<RoomResponseDto> findByCompanyId(Long companyId) {
        List<Room> rooms = roomRepository.findByCompanyId(companyId);
        return rooms.stream()
                .map(room -> RoomResponseDto.builder()
                        .roomId(room.getRoomId())
                        .roomSize(room.getRoomSize())
                        .price(room.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    private Company verifyCompany(String email) {
        return companyRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("유령회사...?"));
    }
}
