package com.doghotel.reservation.domain.post.entity;

import com.doghotel.reservation.domain.post.entity.Posts;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PostsImg {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long postsImgId;

    @Column
    private String originalFilename;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String url;

    @ManyToOne
    @JoinColumn(name = "posts_id")
    private Posts posts;

    @Builder
    public PostsImg(String originalFilename, String name, String url, Posts posts) {
        this.originalFilename = originalFilename;
        this.name = name;
        this.url = url;
        this.posts = posts;
    }

}
