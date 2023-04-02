package com.doghotel.reservation.dog.service;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.service.CustomerVerifyingService;
import com.doghotel.reservation.domain.dog.dto.DogListResponseDto;
import com.doghotel.reservation.domain.dog.dto.DogPostDto;
import com.doghotel.reservation.domain.dog.dto.DogResponseDto;
import com.doghotel.reservation.domain.dog.dto.DogUpdateDto;
import com.doghotel.reservation.domain.dog.entity.Dog;
import com.doghotel.reservation.domain.dog.repository.DogRepository;
import com.doghotel.reservation.domain.dog.service.DogService;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class DogServiceTest {
    @Mock
    private DogRepository dogRepository;
    @Mock
    private AWSS3Service awss3Service;
    @Mock
    private CustomerVerifyingService verifyingService;

    @InjectMocks
    private DogService dogService;

    /*@Test
    void addDogsTest() throws IOException {
        //given
        DogPostDto dogPostDto = DogPostDto.builder()
                .dogName("dog")
                .age(4)
                .weight(4.5)
                .type("푸들")
                .gender("MALE")
                .etc("test etc")
                .build();
        String email = "customer@gmail.com";
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("abcd1234!")
                .username("customer")
                .phone("010-1234-5678")
                .build();
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/test.png");

        String name = "image";
        String originalFilename = "test.png";

        String url = "https://s3.com";

        MultipartFile file = new MockMultipartFile(name,
                originalFilename,
                "png",
                fileInputStream);
        //when
        doReturn(customer)
                .when(verifyingService).findByEmail(email);
        Dog dog = dogPostDto.toEntity();
        dog.designateCustomer(customer);

        doReturn(originalFilename)
                .when(awss3Service).originalFileName(file);
        doReturn(name)
                .when(awss3Service).filename(originalFilename);
        doReturn(url)
                .when(awss3Service).uploadFile(file);

        //then
        String dogName = dogService.addDogs(dogPostDto, email);

        assertThat(dogName).isEqualTo("dog");
    }*/

    @Test
    void updateDogTest() throws IOException {
        //given
        Long dogId = 1L;
        String email = "customer@gmail.com";
        Dog dog = Dog.builder()
                .dogName("dog")
                .type("진돗개")
                .age(5)
                .weight(5.5)
                .gender("FEMALE")
                .etc("test etc")
                .build();
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("abcd1234!")
                .username("customer")
                .phone("010-1234-5678")
                .build();
        DogUpdateDto dto = DogUpdateDto.builder()
                .dogName("update dog")
                .age(6)
                .weight(5.7)
                .type("update type")
                .gender("FEMALE")
                .etc("update etc")
                .build();
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/test.png");

        String name = "image";
        String originalFilename = "test.png";

        String url = "https://s3.com";

        MultipartFile file = new MockMultipartFile(name,
                originalFilename,
                "png",
                fileInputStream);

        //when
        doReturn(customer)
                .when(verifyingService).findByEmail(email);
        doReturn(Optional.ofNullable(dog))
                .when(dogRepository).findById(dogId);
        doReturn(originalFilename)
                .when(awss3Service).originalFileName(file);
        doReturn(name)
                .when(awss3Service).filename(originalFilename);
        doReturn(url)
                .when(awss3Service).uploadFile(file);

        doReturn(dog)
                .when(dogRepository).save(dog);
        //then
        String dogName = dogService.updateDog(dogId, dto, email);

        assertThat(dogName)
                .isEqualTo("update dog");
    }

    @Test
    void showDogByDogIdTest() {
        //given
        Long dogId = 1L;
        String email = "customer@gmail.com";
        Dog dog = Dog.builder()
                .dogName("dog")
                .type("진돗개")
                .age(5)
                .weight(5.5)
                .gender("FEMALE")
                .etc("test etc")
                .build();
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("abcd1234!")
                .username("customer")
                .phone("010-1234-5678")
                .build();
        dog.designateCustomer(customer);

        //when
        doReturn(customer)
                .when(verifyingService).findByEmail(email);
        doReturn(Optional.ofNullable(dog))
                .when(dogRepository).findById(dogId);

        DogResponseDto response = dogService.showDogByDogId(dogId, email);

        //then
        assertThat(response.getDogName()).isEqualTo("dog");
    }

    @Test
    void showMyDogsTest() {
        //given
        String email = "customer@gmail.com";
        Long customerId = 1L;
        Dog dog1 = Dog.builder()
                .dogName("dog1")
                .type("푸들")
                .age(5)
                .weight(5.5)
                .gender("FEMALE")
                .etc("test1 etc")
                .build();
        Dog dog2 = Dog.builder()
                .dogName("dog2")
                .type("진돗개")
                .age(6)
                .weight(6.5)
                .gender("MALE")
                .etc("test2 etc")
                .build();
        Customer customer = Customer.builder()
                .email("customer@gmail.com")
                .password("abcd1234!")
                .username("customer")
                .phone("010-1234-5678")
                .build();
        dog1.designateCustomer(customer);
        dog2.designateCustomer(customer);

        List<Dog> list = List.of(dog1, dog2);
        List<DogListResponseDto> dogListResponseDtos
                = list.stream()
                        .map(DogListResponseDto::of)
                        .collect(Collectors.toList());
        //when
        doReturn(customer)
                .when(verifyingService).findByEmail(email);
        doReturn(list)
                .when(dogRepository).findByCustomerCustomerId(anyLong());
        //then
        List<DogListResponseDto> result = dogService.showMyDogs(email);
        assertThat(dogListResponseDtos.size()).isEqualTo(result.size());
    }

}
