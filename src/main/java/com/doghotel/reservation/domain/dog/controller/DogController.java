package com.doghotel.reservation.domain.dog.controller;

import com.doghotel.reservation.domain.dog.dto.*;
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

    @PostMapping("/dog-image")
    public ResponseEntity<List<DogImageResponseDto>> addDogImage(@RequestPart(name = "file")List<MultipartFile> files,
                                                                @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        List<DogImageResponseDto> dtos;
        try {
            dtos = dogService.addDogs(files, userDetails.getEmail());
        } catch (IllegalArgumentException e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(dtos, HttpStatus.CREATED);
    }

    @PostMapping
    public String addDogs(@RequestBody DogPostRequestDto dogPostRequestDto,
                                  @AuthenticationPrincipal CustomUserDetails customUserDetails) {
        dogService.addDogs(dogPostRequestDto, customUserDetails.getEmail());

        return "dog saved";
    }

    @PatchMapping("/{dog-id}")
    public ResponseEntity<String> updateDog(@PathVariable(name = "dog-id")Long dogId,
                                            @RequestBody DogUpdateDto dto,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        String dogName = dogService.updateDog(dogId, dto, userDetails.getEmail());
        return new ResponseEntity<>(dogName, HttpStatus.OK);
    }

    @GetMapping("/{dog-id}")
    public ResponseEntity<DogResponseDto> showDogByDogId(@PathVariable(name = "dog-id") Long dogId,
                                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        DogResponseDto response = dogService.showDogByDogId(dogId, userDetails.getEmail());

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
