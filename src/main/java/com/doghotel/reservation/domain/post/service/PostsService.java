package com.doghotel.reservation.domain.post.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.post.dto.*;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.entity.PostsImg;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import com.doghotel.reservation.domain.post.repository.PostsRepositoryImpl;
import com.doghotel.reservation.domain.room.dto.RoomDto;
import com.doghotel.reservation.domain.room.dto.RoomResponseDto;
import com.doghotel.reservation.domain.room.service.RoomService;
import com.doghotel.reservation.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.NoSuchElementException;

@Transactional
@Service
@RequiredArgsConstructor
public class PostsService {
    private final PostsRepository postsRepository;
    private final PostsRepositoryImpl postsRepositoryImpl;
    private final CompanyRepository companyRepository;
    private final PostsImgService postsImgService;
    private final RoomService roomService;
    private final TagService tagService;

    public String createPosts(String email, PostsDto dto, List<MultipartFile> multipartFiles) throws IOException {
        Company company = verifyingEmail(email);

        Posts posts = dto.toEntity();
        posts.setCompany(company);

        posts = postsRepository.save(posts);

        saveImages(multipartFiles, posts);

        tagService.createTag(dto.getTagList(), posts);

        return posts.getTitle();
    }

    public void saveImages(List<MultipartFile> fileList, Posts posts) throws IOException {
        if(fileList.size() > 10) {
            throw new IllegalArgumentException("사진은 5장");
        }

        List<PostsImg> postsImgs = postsImgService.savePostsImg(fileList, posts);
        posts.setPostsImgs(postsImgs);
    }

    public String updatePosts(String email, Long postsId, PostsUpdateDto dto, List<MultipartFile> files) throws IOException {
        Company company = verifyingEmail(email);
        Posts posts = findPosts(postsId);
        if(!posts.getCompany().equals(company)) {
            throw new IllegalArgumentException("본인의 포스트만 수정 가능합니다.");
        }
        posts.updatePosts(dto);

        List<PostsImg> postsImgs = postsImgService.updatePostsImg(files, posts);
        posts.setPostsImgs(postsImgs);

        tagService.updateTags(dto.getTagList(), posts);

        return posts.getTitle();
    }

    public PostsResponseDto getPost(Long postsId) {
        Posts posts = findPosts(postsId);
        List<PostsImgDto> postsImgDtos = postsImgService.findPostsImgById(postsId);
        List<String> tags = tagService.findTagsByPostsId(postsId);
        List<RoomResponseDto> roomDtos = roomService.findByCompanyId(posts.getCompany().getCompanyId());

        String checkInStartTime = posts.getCheckInStartTime().format(DateTimeFormatter.ISO_LOCAL_DATE);
        String checkInEndTime = posts.getCheckInEndTime().format(DateTimeFormatter.ISO_LOCAL_DATE);

        return PostsResponseDto.builder()
                .postsId(postsId)
                .title(posts.getTitle())
                .latitude(posts.getLatitude())
                .longitude(posts.getLongitude())
                .address(posts.getAddress())
                .phoneNumber(posts.getPhoneNumber())
                .checkInStartTime(checkInStartTime)
                .checkInEndTime(checkInEndTime)
                .tags(tags)
                .score(posts.getScore())
                .postsImgDtos(postsImgDtos)
                .roomResponseDtos(roomDtos)
                .build();
    }

    public Page<PostsResponsesDto> getMainPages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        Page<PostsResponsesDto> postsResponsesDtos = postsRepositoryImpl.getMainPages(pageable);

        return postsResponsesDtos;
    }
    public Page<PostsResponsesDto> searchPages(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        Page<PostsResponsesDto> postsResponsesDtos = postsRepositoryImpl.searchPagesByTitleOrContentOrAddress(keyword, pageable);

        return postsResponsesDtos;
    }

    public void deletePosts(String email, Long postsId) {
        Company company = verifyingEmail(email);
        company.deletePosts();
        postsRepository.deleteById(postsId);
    }

    private Posts findPosts(Long postsId) {
        return postsRepository.findById(postsId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 게시글"));
    }

    private Company verifyingEmail(String email) {
        return companyRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회사"));
    }
}
