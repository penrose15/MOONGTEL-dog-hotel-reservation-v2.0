package com.doghotel.reservation.domain.post.controller;

import com.doghotel.reservation.domain.post.dto.PostsDto;
import com.doghotel.reservation.domain.post.dto.PostsImgIdsDto;
import com.doghotel.reservation.domain.post.dto.PostsResponsesDto;
import com.doghotel.reservation.domain.post.dto.PostsUpdateDto;
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
                                      @RequestBody PostsUpdateDto dto,
                                      @AuthenticationPrincipal CustomUserDetails userDetails) {
        String email = userDetails.getUsername();
        String title = postsService.updatePosts(email, postsId, dto);

        return title;
    }

    @PatchMapping("/image/{post-id}") //다시 만들기 -> 사진 리스트 들어오면 사진이랑
    public ResponseEntity<Void> updatePostsImg(@PathVariable(name = "post-id") Long postsId,
                               @RequestPart(value = "dto") PostsImgIdsDto dto,
                               @RequestPart(value = "files") List<MultipartFile> files,
                               @AuthenticationPrincipal CustomUserDetails userDetails) throws IOException {
        String email = userDetails.getUsername();
        postsService.updatePostsImg(dto.getPostsImgIds(), files, postsId);

        return new ResponseEntity<>(HttpStatus.OK);
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
