package com.doghotel.reservation.domain.dog.service;

import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.service.CustomerVerifyingService;
import com.doghotel.reservation.domain.dog.dto.*;
import com.doghotel.reservation.domain.dog.entity.Dog;
import com.doghotel.reservation.domain.dog.entity.DogImage;
import com.doghotel.reservation.domain.dog.repository.DogImageRepository;
import com.doghotel.reservation.domain.dog.repository.DogRepository;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import com.doghotel.reservation.global.exception.BusinessLogicException;
import com.doghotel.reservation.global.exception.ExceptionCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Transactional
@Service
public class DogService{
    private final AWSS3Service awss3Service;
    private final DogRepository dogRepository;
    private final DogImageRepository dogImageRepository;
    private final CustomerVerifyingService verifyingService;


    public List<DogImageResponseDto> addDogs(List<MultipartFile> files, String email) throws IOException {
        Customer customer = verifyingService.findByEmail(email);

        if(files.size() > 5) {
            throw new IllegalArgumentException("사진은 5장 이내로");
        }

        List<DogImageResponseDto> dtos = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalFileName = awss3Service.originalFileName(file);
            String filename = awss3Service.filename(originalFileName);
            String url = awss3Service.uploadFile(file);
            DogImageResponseDto dto = DogImageResponseDto.builder()
                    .originalFilename(originalFileName)
                    .fileName(filename)
                    .url(url)
                    .build();
            dtos.add(dto);
        }
        return dtos;
    }

    public String addDogs(DogPostRequestDto dogDto, String email) {
        Customer customer = verifyingService.findByEmail(email);

        DogPostDto dogPostDto = dogDto.getDogPostDto();
        List<DogImageResponseDto> dogImageResponseDtos = dogDto.getDogImageResponseDtos();

        Dog dog = dogPostDto.toEntity();
        dog.designateCustomer(customer);
        dog = dogRepository.save(dog);

        List<DogImage> dogImages = new ArrayList<>();
        for (DogImageResponseDto dogImageResponseDto : dogImageResponseDtos) {
            DogImage dogImage = DogImage.builder()
                    .originalFilename(dogImageResponseDto.getOriginalFilename())
                    .fileName(dogImageResponseDto.getFileName())
                    .url(dogImageResponseDto.getUrl())
                    .dog(dog)
                    .build();

            dogImages.add(dogImage);
        }
        dogImageRepository.saveAll(dogImages);

        return "save dog";
    }

    public String updateDog(Long dogId, DogUpdateDto dto, String email) throws IOException {
        Customer customer = verifyingService.findByEmail(email); //verify
        Dog dog = findDogById(dogId);

        dog.updateDog(dto);

        dog = dogRepository.save(dog);

        return dog.getDogName();
    }



    public DogResponseDto showDogByDogId(Long dogId, String email) {
        Customer customer = verifyingService.findByEmail(email);
        Dog dog = findDogById(dogId);

        if(!dog.getCustomer().equals(customer)) {
            throw new BusinessLogicException(ExceptionCode.ONLY_EDIT_SELECT_DELETE_YOUR_PUPPY);
        }

        return DogResponseDto.of(dog);
    }

    public List<DogListResponseDto> showMyDogs(String email) {
        Customer customer = verifyingService.findByEmail(email);
        List<Dog> list = dogRepository.findByCustomerCustomerId(customer.getCustomerId());

        return list.stream()
        .map(DogListResponseDto::of)
        .collect(Collectors.toList());
    }

    public void deleteDog(long dogId, String email) {
        Dog dog = findDogById(dogId);
        Customer customer = verifyingService.findByEmail(email);
        if(!dog.getCustomer().equals(customer)) {
            throw new BusinessLogicException(ExceptionCode.ONLY_EDIT_SELECT_DELETE_YOUR_PUPPY);
        }

        dogRepository.deleteById(dogId);
    }

    private Dog findDogById(long dogId) {
        return dogRepository.findById(dogId)
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.DOG_NOT_FOUND));
    }


}
