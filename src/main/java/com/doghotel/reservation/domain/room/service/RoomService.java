package com.doghotel.reservation.domain.room.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.room.dto.RoomDto;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Transactional
@RequiredArgsConstructor
@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final CompanyRepository companyRepository;

    public String createRoom(List<RoomDto> roomDtos, String email) {
        Company company = verifyCompany(email);
        for(int i = 0; i<roomDtos.size(); i++) {
            Room room = roomDtos.get(i).toEntity();
            room.setCompany(company);
            roomRepository.save(room);
        }
        return "save room";
    }

    private Company verifyCompany(String email) {
        return companyRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("유령회사...?"));
    }
}
