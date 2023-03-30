package com.doghotel.reservation.tag.repository;

import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import com.doghotel.reservation.domain.tag.entity.PostsTagMap;
import com.doghotel.reservation.domain.tag.entity.Tag;
import com.doghotel.reservation.domain.tag.repository.PostTagMapRepository;
import com.doghotel.reservation.domain.tag.repository.TagRepository;
import com.doghotel.reservation.global.querydsl.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({QueryDslConfig.class})
public class PostTagMapRepositoryTest {

    @Autowired
    private PostTagMapRepository postTagMapRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private PostsRepository postsRepository;

    private Posts posts;
    private Tag tag1;
    private Tag tag2;
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
        posts = postsRepository.save(posts);
        tag1 = Tag.builder()
                .title("test tag1")
                .build();
        tag2 = Tag.builder()
                .title("test tag2")
                .build();
        tag1 = tagRepository.save(tag1);
        tag2 = tagRepository.save(tag2);

        PostsTagMap postsTagMap1 = PostsTagMap.builder()
                .tag(tag1)
                .posts(posts)
                .build();
        PostsTagMap postsTagMap2 = PostsTagMap.builder()
                .tag(tag2)
                .posts(posts)
                .build();
        postTagMapRepository.save(postsTagMap1);
        postTagMapRepository.save(postsTagMap2);
    }

    @Test
    void findByPostsPostsIdTest() {
        List<PostsTagMap> postsTagMaps = postTagMapRepository.findByPostsPostsId(posts.getId());

        assertThat(postsTagMaps.size())
                .isEqualTo(2);
    }
}
