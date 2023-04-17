package com.doghotel.reservation.dog.repository;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.repository.CustomerRepository;
import com.doghotel.reservation.domain.dog.entity.Dog;
import com.doghotel.reservation.domain.dog.repository.DogRepository;
import com.doghotel.reservation.global.querydsl.QueryDslConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class DogRepositoryTest {
    @Autowired
    private DogRepository dogRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findByCustomerCustomerId() {
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("password1234!")
                .username("customer")
                .phone("010-1111-2222")
                .build();

        customer = customerRepository.save(customer);

        Dog dog1 = Dog.builder()
                .dogName("dog1")
                .type("푸들")
                .gender("MALE")
                .age(4)
                .weight(4.5)
                .etc("중성화 수술 함, 당뇨 있음")
                .build();
        dog1.designateCustomer(customer);
        Dog dog2 = Dog.builder()
                .dogName("dog2")
                .type("말티즈")
                .gender("FEMALE")
                .age(7)
                .weight(3.0)
                .etc("쓸개골 탈골")
                .build();
        dog2.designateCustomer(customer);

        dog1 = dogRepository.save(dog1);
        dog2 = dogRepository.save(dog2);

        List<Dog> dogs = List.of(dog1, dog2);

        List<Dog> response = dogRepository.findByCustomerCustomerId(customer.getCustomerId());

        assertThat(response).isEqualTo(dogs);


    }
}
