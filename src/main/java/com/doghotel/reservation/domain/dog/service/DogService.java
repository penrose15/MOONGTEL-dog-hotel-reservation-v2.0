package com.doghotel.reservation.domain.dog.service;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.service.CustomerVerifyingService;
import com.doghotel.reservation.domain.dog.dto.DogListResponseDto;
import com.doghotel.reservation.domain.dog.dto.DogPostDto;
import com.doghotel.reservation.domain.dog.dto.DogResponseDto;
import com.doghotel.reservation.domain.dog.dto.DogUpdateDto;
import com.doghotel.reservation.domain.dog.entity.Dog;
import com.doghotel.reservation.domain.dog.repository.DogRepository;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class DogService {
    private final DogRepository dogRepository;
    private final AWSS3Service awss3Service;
    private final CustomerVerifyingService verifyingService;

    public String addDogs(DogPostDto dogPostDto, MultipartFile file, String email) {
        Customer customer = verifyingService.findByEmail(email);
        Dog dog = dogPostDto.toEntity();
        dogRepository.save(dog);
        dog.designateCustomer(customer);

        String filename = awss3Service.originalFileName(file);
        String url = awss3Service.originalFileName(file);

        dog.addImage(filename, url);

        return dog.getDogName();
    }

    public void updateDog(long dogId, DogUpdateDto dto, MultipartFile file, String email) {
        Customer customer = verifyingService.findByEmail(email); //verify
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강아지"));

        String filename = awss3Service.originalFileName(file);
        String url = awss3Service.originalFileName(file);

        dog.updateDog(dto, filename, url);

        dogRepository.save(dog);
    }

    public DogResponseDto findById(Long dogId) {
        Dog dog = dogRepository.findById(dogId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 강아지"));

        return DogResponseDto.of(dog);
    }

    public List<DogListResponseDto> findAll(String email) {
        Customer customer = verifyingService.findByEmail(email);
        List<Dog> list = dogRepository.findByCustomerCustomerId(customer.getCustomerId());

        return list.stream()
        .map(DogListResponseDto::of)
        .collect(Collectors.toList());
    }

    public void deleteDog(Long dogId) {
        dogRepository.deleteById(dogId);
    }


}
