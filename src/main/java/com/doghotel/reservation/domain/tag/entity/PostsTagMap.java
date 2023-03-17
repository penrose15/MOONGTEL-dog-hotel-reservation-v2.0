package com.doghotel.reservation.domain.tag.entity;

import com.doghotel.reservation.domain.post.entity.Posts;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostsTagMap {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "postsId")
    private Posts posts;

    @ManyToOne(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "tagId")
    private Tag tag;

    @Builder
    public PostsTagMap(Posts posts, Tag tag) {
        this.posts = posts;
        this.tag = tag;
    }
}
