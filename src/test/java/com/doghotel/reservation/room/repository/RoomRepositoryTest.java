package com.doghotel.reservation.room.repository;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import com.doghotel.reservation.global.querydsl.QueryDslConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class RoomRepositoryTest {
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private CompanyRepository companyRepository;

    @Test
    void findByCompanyId() {
        Company company = Company.builder()
                .email("company@gmail.com")
                .password("1234abcd!")
                .companyName("test company")
                .address("test address")
                .detailAddress("test detailAddress")
                .representativeNumber("123456789")
                .build();
        company = companyRepository.save(company);
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

        List<Room> roomList = roomRepository.findByCompanyId(company.getCompanyId());

        assertThat(roomList.size())
                .isEqualTo(2);
    }

}
