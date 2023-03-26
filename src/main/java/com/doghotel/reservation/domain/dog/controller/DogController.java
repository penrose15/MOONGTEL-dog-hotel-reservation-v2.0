package com.doghotel.reservation.domain.dog.controller;

import com.doghotel.reservation.domain.dog.dto.DogListResponseDto;
import com.doghotel.reservation.domain.dog.dto.DogPostDto;
import com.doghotel.reservation.domain.dog.dto.DogResponseDto;
import com.doghotel.reservation.domain.dog.dto.DogUpdateDto;
import com.doghotel.reservation.domain.dog.service.DogService;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RequestMapping("/v1/dog")
@RestController
public class DogController {
    private final DogService dogService;

    @PostMapping
    public ResponseEntity<String> addDog(@RequestPart(name = "dto") @Valid DogPostDto dto,
                                 @RequestPart(name = "file")MultipartFile file,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        String dogName = dogService.addDogs(dto, file, userDetails.getEmail());
        return new ResponseEntity<>(dogName, HttpStatus.CREATED);
    }

    @PatchMapping("/{dog-id}")
    public ResponseEntity<String> updateDog(@PathVariable(name = "dog-id")Long dogId,
                                            @RequestPart(name = "dto")DogUpdateDto dto,
                                            @RequestPart(name = "file") MultipartFile file,
                                            @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        String dogName = dogService.updateDog(dogId, dto, file, userDetails.getEmail());
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
