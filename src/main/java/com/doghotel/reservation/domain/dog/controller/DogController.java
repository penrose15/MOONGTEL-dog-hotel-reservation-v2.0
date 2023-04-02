package com.doghotel.reservation.domain.dog.controller;

import com.doghotel.reservation.domain.dog.dto.*;
import com.doghotel.reservation.domain.dog.service.DogImageService;
import com.doghotel.reservation.domain.dog.service.DogService;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RequestMapping("/v1/dog")
@RestController
public class DogController {
    private final DogService dogService;
    private final DogImageService dogImageService;

    @PostMapping("/dog-image")
    public ResponseEntity<List<DogImageResponseDto>> addDogImages(@RequestPart List<MultipartFile> file,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        List<DogImageResponseDto> dtos;
        try {
            dtos = dogService.addDogs(file, userDetails.getEmail());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dtos, HttpStatus.CREATED);
    }

    @PostMapping
    public ResponseEntity addDogs(@RequestBody DogPostRequestDto dogPostRequestDto,
                                  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        dogService.addDogs(dogPostRequestDto, customUserDetails.getEmail());

        return new ResponseEntity("dog saved",HttpStatus.CREATED);
    }

    @PatchMapping("/{dog-id}")
    public ResponseEntity<String> updateDog(@PathVariable(name = "dog-id")Long dogId,
                                            @RequestBody DogUpdateDto dto,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        String dogName = dogService.updateDog(dogId, dto, userDetails.getEmail());
        return new ResponseEntity<>(dogName, HttpStatus.OK);
    }

    @PostMapping("/dog-image/{dog-id}")
    public ResponseEntity updateDogImage(@PathVariable(name = "dog-id")Long dogId,
                                         @RequestPart(name = "files")List<MultipartFile> files,
                                         @AuthenticationPrincipal CustomUserDetails userDetails) {
        dogImageService.updateFiles(dogId, files);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{dog-id}")
    public ResponseEntity<DogDetailProfileDto> showDogByDogId(@PathVariable(name = "dog-id") Long dogId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        DogResponseDto dogResponseDto = dogService.showDogByDogId(dogId, userDetails.getEmail());
        List<DogImageResponseDto> imageResponses = dogImageService.findDogImages(dogId);

        DogDetailProfileDto response = new DogDetailProfileDto(dogResponseDto, imageResponses);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/dogs")
    public ResponseEntity<List<DogListResponseDto>> showMyDogs(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<DogListResponseDto> responses = dogService.showMyDogs(userDetails.getEmail());

        return new ResponseEntity<>(responses, HttpStatus.OK);
    }

    @DeleteMapping("/{dog-id}")
    public ResponseEntity<Void> deleteDog(@PathVariable(name = "dog-id")Long dogId,
                                          @AuthenticationPrincipal CustomUserDetails userDetails) {
        dogService.deleteDog(dogId, userDetails.getEmail());

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
