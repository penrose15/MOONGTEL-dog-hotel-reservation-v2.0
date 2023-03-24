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
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PostsImgService {
    private final PostsImgRepository postsImgRepository;
    private final AWSS3Service awss3Service;

    public List<PostsImg> savePostsImg(List<MultipartFile> multipartFiles, Posts posts) throws IOException {
        List<PostsImg> postsImgs = new ArrayList<>();
        for(int i = 0; i<multipartFiles.size();i++) {
            String originalFileName = awss3Service.originalFileName(multipartFiles.get(i));
            String fileName = awss3Service.filename(originalFileName);
            String url = awss3Service.uploadFile(multipartFiles.get(i));

            PostsImg postsImg = PostsImg.builder()
                    .originalFilename(originalFileName)
                    .name(fileName)
                    .url(url)
                    .posts(posts)
                    .build();
            postsImg = postsImgRepository.save(postsImg);
            postsImgs.add(postsImg);
        }
        return postsImgs;
    }

    public List<PostsImg> updatePostsImg(List<MultipartFile> files, Posts posts) throws IOException {
        List<PostsImg> postsImgs = postsImgRepository.findByPostsId(posts.getId());
        List<Integer> deleteIdx = new ArrayList<>(); //postImgs의 idx
        List<Integer> insertIdx = new ArrayList<>(); //files의 idx
        List<Integer> skipIdx = new ArrayList<>();

        List<String> originalFileNames = files.stream()
                .map(awss3Service::originalFileName)
                .collect(Collectors.toList());
        List<String> existFilename = postsImgs.stream()
                .map(PostsImg::getOriginalFilename)
                .collect(Collectors.toList());

        for(int i = 0; i<postsImgs.size(); i++) {
            if(!originalFileNames.contains(postsImgs.get(i).getOriginalFilename())) {
                deleteIdx.add(i);
            }
            else {
                skipIdx.add(i);
            }
        }
        for(int i = 0; i<originalFileNames.size(); i++) {
            if(!existFilename.contains(originalFileNames.get(i))) {
                insertIdx.add(i);
            }
        }
        //delete file
        for(int i = 0; i<deleteIdx.size(); i++) {
            int idx = deleteIdx.get(i);
            awss3Service.deleteFile(postsImgs.get(idx).getName()); // file delete
            postsImgRepository.deleteById(postsImgs.get(idx).getPostsImgId()); //db delete
        }
        List<PostsImg> postsImgsList = new ArrayList<>();
        //save file
        for(int i = 0; i<insertIdx.size(); i++) {
            int idx = insertIdx.get(i);
            String originalFilename = awss3Service.originalFileName(files.get(idx));
            String filename = awss3Service.filename(originalFilename);
            String url = awss3Service.uploadFile(files.get(idx));

            PostsImg postsImg = PostsImg.builder()
                    .originalFilename(originalFilename)
                    .name(filename)
                    .url(url)
                    .posts(posts)
                    .build();
            postsImg = postsImgRepository.save(postsImg);
            postsImgsList.add(postsImg);
        }

        for(int i = 0; i<skipIdx.size(); i++) {
            int idx = skipIdx.get(i);
            postsImgsList.add(postsImgs.get(idx));
        }

        return postsImgsList;
    }

    public List<PostsImgDto> findPostsImgById(Long postsId) {
        List<PostsImg> postsImgs = postsImgRepository.findByPostsId(postsId);
        return postsImgs.stream()
                .map(img -> PostsImgDto.builder()
                        .postsImgId(img.getPostsImgId())
                        .filename(img.getName())
                        .url(img.getUrl())
                        .build()).collect(Collectors.toList());
    }
}
