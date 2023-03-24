package com.doghotel.reservation.domain.post.controller;

import com.doghotel.reservation.domain.post.dto.*;
import com.doghotel.reservation.domain.post.service.PostsService;
import com.doghotel.reservation.domain.tag.service.TagService;
import com.doghotel.reservation.global.config.security.userdetail.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequestMapping("/v1/post")
@RestController
@RequiredArgsConstructor
public class PostsController {
    private final PostsService postsService;

    @PostMapping
    public ResponseEntity<String> createPosts(@RequestPart(value = "dto")PostsDto dto,
                                              @RequestPart(value = "files")List<MultipartFile> files,
                                              @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        String email = userDetails.getUsername();
        String title = postsService.createPosts(email, dto, files);

        return new ResponseEntity<>(title, HttpStatus.CREATED);
    }

    @PatchMapping("/{post-id}")
    public String updatePosts(@PathVariable(name = "post-id") Long postsId,
                                      @RequestPart(name = "dto") PostsUpdateDto dto,
                                      @RequestPart(name = "file") List<MultipartFile> file,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        String email = userDetails.getUsername();

        return postsService.updatePosts(email, postsId, dto, file);
    }

    @GetMapping("/{posts-id}")
    public ResponseEntity showPost(@PathVariable(name = "posts-id")Long postsId) {
        PostsResponseDto response = postsService.getPost(postsId);

        return new ResponseEntity(response, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity showPosts(@RequestParam int page,
                                    @RequestParam int size) {
        Page<PostsResponsesDto> postsResponsesDtos = postsService.getMainPages(page-1, size);
        List<PostsResponsesDto> postsResponsesDtoList = postsResponsesDtos.getContent();

        return new ResponseEntity(postsResponsesDtoList, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity searchPost(@RequestParam int page,
                                     @RequestParam int size,
                                     @RequestParam String keyword) {
        Page<PostsResponsesDto> postsResponsesDtos = postsService.searchPages(page-1, size, keyword);
        List<PostsResponsesDto> postsResponsesDtoList = postsResponsesDtos.getContent();

        return new ResponseEntity(postsResponsesDtoList, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity delete(@PathVariable("id") Long postsId,
                                 @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        postsService.deletePosts(email, postsId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
