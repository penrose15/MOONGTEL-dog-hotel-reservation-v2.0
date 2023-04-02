package com.doghotel.reservation.domain.dog.service;

import com.doghotel.reservation.domain.customer.service.CustomerVerifyingService;
import com.doghotel.reservation.domain.dog.dto.DogImageResponseDto;
import com.doghotel.reservation.domain.dog.entity.Dog;
import com.doghotel.reservation.domain.dog.entity.DogImage;
import com.doghotel.reservation.domain.dog.repository.DogImageRepository;
import com.doghotel.reservation.domain.dog.repository.DogRepository;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import com.doghotel.reservation.infra.updateImgFile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
@RequiredArgsConstructor
public class DogImageService extends updateImgFile<DogImage, DogImageResponseDto> {
    private final AWSS3Service awss3Service;
    private final DogImageRepository dogImageRepository;

    @Override
    public void updateFiles(Long id, List<MultipartFile> files) {
        List<DogImage> dogImages = findById(id);
        List<DogImageResponseDto> dtos = convertImageToDTOs(files);
        deleteList(dogImages, dtos);
        addList(dogImages, dtos);
    }

    @Override
    public List<DogImage> findById(Long id) {
        return dogImageRepository.findByDogId(id);
    }

    @Override
    public List<DogImageResponseDto> convertImageToDTOs(List<MultipartFile> files) {
        return files.stream()
                .map(file -> {
                    String originalFilename = file.getOriginalFilename();
                    String filename = awss3Service.filename(originalFilename);
                    String url;
                    try {
                        url = awss3Service.uploadFile(file);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    return DogImageResponseDto.builder()
                            .originalFilename(originalFilename)
                            .fileName(filename)
                            .url(url)
                            .build();
                }).collect(Collectors.toList());
    }

    @Override
    public void deleteList(List<DogImage> dogImages, List<DogImageResponseDto> dtos) {
        List<DogImage> deleteList = new ArrayList<>();
        for (DogImage dogImage : dogImages) {
            DogImageResponseDto dto = DogImageResponseDto.builder()
                    .originalFilename(dogImage.getOriginalFilename())
                    .fileName(dogImage.getFileName())
                    .url(dogImage.getUrl())
                    .build();
            boolean found = false;
            for (DogImageResponseDto dogImageResponseDto : dtos) {
                if(dogImageResponseDto.equals(dto)) {
                    found = true;
                    break;
                }
            }
            if(!found) {
                deleteList.add(dogImage);
            }
        }
        dogImageRepository.deleteAll(deleteList);

    }

    @Override
    public void addList(List<DogImage> dogImages, List<DogImageResponseDto> dtos) {
        Dog dog = dogImages.get(0).getDog();
        List<DogImageResponseDto> insertList = new ArrayList<>();
        for (DogImageResponseDto dto : dtos) {
            boolean found = false;
            for (DogImage dogImage : dogImages) {
                DogImageResponseDto dogImageResponseDto = DogImageResponseDto.builder()
                        .originalFilename(dogImage.getOriginalFilename())
                        .fileName(dogImage.getFileName())
                        .url(dogImage.getUrl())
                        .build();
                if (dto.equals(dogImageResponseDto)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                insertList.add(DogImageResponseDto.builder()
                        .originalFilename(dto.getOriginalFilename())
                        .fileName(dto.getFileName())
                        .url(dto.getUrl())
                        .build());
            }
        }
        List<DogImage> insertDogImage = insertList.stream()
                .map(DogImageResponseDto::toEntity)
                .map(dogImage -> dogImage.addDog(dog))
                .collect(Collectors.toList());
        dogImageRepository.saveAll(insertDogImage);

    }
}
