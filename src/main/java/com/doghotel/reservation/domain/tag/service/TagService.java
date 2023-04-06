package com.doghotel.reservation.domain.tag.service;

import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.tag.entity.PostsTagMap;
import com.doghotel.reservation.domain.tag.entity.Tag;
import com.doghotel.reservation.domain.tag.repository.PostTagMapRepository;
import com.doghotel.reservation.domain.tag.repository.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class TagService {
    private final PostTagMapRepository postTagMapRepository;
    private final TagRepository tagRepository;

    public String createTag(List<String> tags, Posts posts) {
        if(tags.size() >10 || tags.size() < 1) {
            throw new IllegalArgumentException("태그는 10개 이하로만 작성해주세요");
        }

        List<Tag> tagList = new ArrayList<>();
        for(int i = 0; i<tags.size(); i++) {
            Tag tag = Tag.builder()
                    .title(tags.get(i))
                    .build();

            tagList.add(tag);
        }
        tagRepository.saveAll(tagList);
        List<PostsTagMap> postsTagMapList = new ArrayList<>();
        for(int i = 0; i<tagList.size();i++) {
            PostsTagMap postsTagMap = PostsTagMap.builder()
                    .posts(posts)
                    .tag(tagList.get(i))
                    .build();
            postsTagMapList.add(postsTagMap);
        }
        postTagMapRepository.saveAll(postsTagMapList);
        return "save tags";

    }


    public String updateTags(List<String> tags, Posts post) {
        Long postsId = post.getId();

        List<PostsTagMap> deleteList = new ArrayList<>();
        List<String> insertList = new ArrayList<>();


        List<PostsTagMap> postsTagMaps = postTagMapRepository.findByPostsPostsId(postsId);

        List<String> tagList = postsTagMaps.stream()
                .map(tag -> tag.getTag().getTitle())
                .collect(Collectors.toList());

        for(int i = 0; i<postsTagMaps.size(); i++) {
            PostsTagMap postsTagMap = postsTagMaps.get(i);
            String tag = postsTagMap.getTag().getTitle();

            if (!tags.contains(tag)) {
                deleteList.add(postsTagMap);
            }
        }
        for(int i = 0; i<tags.size(); i++) {
            String tag = tags.get(i);
            if(!tagList.contains(tag)) {
                insertList.add(tag);
            }
        }
        for(int i = 0; i<deleteList.size(); i++) {
            PostsTagMap postsTagMap = deleteList.get(i);
            Tag tag = postsTagMap.getTag();

            tagRepository.delete(tag);
            postTagMapRepository.delete(postsTagMap);
        }
        if(insertList.size() > 0) {
            createTag(insertList, post);
        }
        return "update tag";
    }

    public List<String> findTagsByPostsId(Long postsId) {
        return tagRepository.findTagsByPostsId(postsId);
    }



}
