package com.doghotel.reservation.domain.post.service;

import com.doghotel.reservation.domain.post.dto.PostsImgDto;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.entity.PostsImg;
import com.doghotel.reservation.domain.post.repository.PostsImgRepository;
import com.doghotel.reservation.global.aws.service.AWSS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class PostsImgService {
    private final PostsImgRepository postsImgRepository;
    private final AWSS3Service awss3Service;

    public List<PostsImg> savePostsImg(List<MultipartFile> multipartFiles, Posts posts) throws IOException {
        List<PostsImg> postsImgs = new ArrayList<>();
        for(int i = 0; i<multipartFiles.size();i++) {
            String fileName = awss3Service.originalFileName(multipartFiles.get(i));
            String url = awss3Service.uploadFile(multipartFiles.get(i));

            PostsImg postsImg = PostsImg.builder()
                    .name(fileName)
                    .url(url)
                    .posts(posts)
                    .build();
            postsImg = postsImgRepository.save(postsImg);
            postsImgs.add(postsImg);
        }
        return postsImgs;
    }

    public List<PostsImg> updatePostsImg(List<MultipartFile> multipartFiles, List<Long> postsImgIds, Long postsId) throws IOException {
        for(int i = 0; i<postsImgIds.size(); i++) {
            Long postsImgId = postsImgIds.get(i);
            PostsImg postsImg = postsImgRepository.findById(postsImgId)
                    .orElseThrow(() -> new NoSuchElementException("존재하지 않는 이미지"));
            String filename = awss3Service.originalFileName(multipartFiles.get(i));
            String url = awss3Service.uploadFile(multipartFiles.get(i));

            postsImg = postsImg.updatePostsImg(filename, url);
            postsImgRepository.save(postsImg);
        }
        return postsImgRepository.findByPostsId(postsId);
    }

//    public PostsImgDto getPostsImgDto(Long postsId) {
//
//    }
}
