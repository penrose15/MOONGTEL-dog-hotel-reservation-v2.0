package com.doghotel.reservation.domain.post.service;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.post.dto.PostsDto;
import com.doghotel.reservation.domain.post.dto.PostsResponsesDto;
import com.doghotel.reservation.domain.post.dto.PostsUpdateDto;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.entity.PostsImg;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import com.doghotel.reservation.domain.post.repository.PostsRepositoryImpl;
import com.doghotel.reservation.domain.tag.service.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
        if(fileList.size() != 5) {
            throw new IllegalArgumentException("사진은 5장");
        }

        List<PostsImg> postsImgs = postsImgService.savePostsImg(fileList, posts);
        posts.setPostsImgs(postsImgs);
    }

    public String updatePosts(String email, Long postsId, PostsUpdateDto dto) {
        Company company = verifyingEmail(email);
        Posts posts = postsRepository.findById(postsId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 포스트"));
        if(!posts.getCompany().equals(company)) {
            throw new IllegalArgumentException("본인의 포스트만 수정 가능합니다.");
        }
        posts.updatePosts(dto);

        tagService.updateTags(dto.getTagList(), posts);

        return posts.getTitle();
    }

    public void updatePostsImg(List<Long> postsImgIds, List<MultipartFile> files, Long postsId) throws IOException {

        List<PostsImg> postsImgs = postsImgService.updatePostsImg(files, postsImgIds, postsId);
        Posts posts = postsRepository.findById(postsId)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 포스트"));
        posts.setPostsImgs(postsImgs);
    }

    public Page<PostsResponsesDto> getMainPages(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.Direction.DESC, "id");
        Page<PostsResponsesDto> postsResponsesDtos = postsRepositoryImpl.getMainPages(pageable);

        return postsResponsesDtos;
    }

    public void deletePosts(String email, Long postsId) {
        Company company = verifyingEmail(email);
        company.deletePosts();
        postsRepository.deleteById(postsId);
    }

    private Company verifyingEmail(String email) {
        return companyRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("존재하지 않는 회사"));
    }
}
