package com.doghotel.reservation.tag.service;

import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.tag.entity.PostsTagMap;
import com.doghotel.reservation.domain.tag.entity.Tag;
import com.doghotel.reservation.domain.tag.repository.PostTagMapRepository;
import com.doghotel.reservation.domain.tag.repository.TagRepository;
import com.doghotel.reservation.domain.tag.service.TagService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class TagServiceTest {
    @Mock
    private PostTagMapRepository postTagMapRepository;
    @Mock
    private TagRepository tagRepository;
    @InjectMocks
    private TagService tagService;

    private Posts posts;
    private List<String> tagList = List.of("tag1", "tag2");
    private List<Tag> tags;
    private List<PostsTagMap> postsTagMapList;

    @BeforeEach
    void init() {
        posts = Posts.builder()
                .title("title")
                .content("content")
                .latitude("latitude")
                .longitude("longitude")
                .phoneNumber("010-1111-2222")
                .address("address")
                .checkInStartTime(LocalTime.of(8,0))
                .checkInEndTime(LocalTime.of(23,0))
                .build();
        tags = new ArrayList<>();
        for (String s : tagList) {
            Tag tag = Tag.builder()
                    .title(s)
                    .build();
            tags.add(tag);
        }
        postsTagMapList = new ArrayList<>();
        for (Tag tag : tags) {
            PostsTagMap postsTagMap = PostsTagMap.builder()
                    .posts(posts)
                    .tag(tag)
                    .build();
            postsTagMapList.add(postsTagMap);
        }
    }

    @Test
    void createTagTest() {
        //when
        doReturn(tags)
                .when(tagRepository).saveAll(anyList());
        doReturn(postsTagMapList)
                .when(postTagMapRepository).saveAll(anyList());
        //then
        String result = tagService.createTag(tagList, posts);
        assertThat(result)
                .isEqualTo("save tags");
    }

    @Test
    void updateTags() {
        //given
        List<String> updateTag = List.of("tag1", "update tag2", "update tag3");
        Tag tag = Tag.builder()
                .title("tag2")
                .build();
        PostsTagMap postsTagMap = PostsTagMap.builder()
                .tag(tag)
                .posts(posts)
                .build();
        List<String> insertList = List.of("update tag2", "update tag3");
        List<Tag> tagList1 = insertList.stream()
                        .map(s -> Tag.builder().title(s).build())
                                .collect(Collectors.toList());
        List<PostsTagMap> postsTagMapList1 = tagList1.stream()
                        .map(t -> PostsTagMap.builder()
                                .posts(posts)
                                .tag(t)
                                .build())
                                .collect(Collectors.toList());

        //when
        doReturn(postsTagMapList)
                .when(postTagMapRepository).findByPostsPostsId(anyLong());
        doNothing()
                .when(tagRepository).delete(any(Tag.class));
        doNothing()
                .when(postTagMapRepository).delete(any(PostsTagMap.class));
        doReturn(tagList1)
                .when(tagRepository).saveAll(anyList());
        doReturn(postsTagMapList1)
                .when(postTagMapRepository).saveAll(anyList());

        String result = tagService.updateTags(updateTag, posts);
        assertThat(result)
                .isEqualTo("update tag");
    }

//    @Test
//    void findTagsByPostsIdTest() {
//        doReturn(tagList)
//                .when(postTagMapRepository).findByPostsPostsId(1L);
//        List<String> result = tagService.findTagsByPostsId(1L);
//
//        assertThat(result.size())
//                .isEqualTo(2);
//    }
}
