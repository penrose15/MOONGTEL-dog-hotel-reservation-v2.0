package com.doghotel.reservation.domain.post.repository;

import com.doghotel.reservation.domain.post.entity.PostsImg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostsImgRepositoryCustom {
    Page<PostsImg> postsImgPage(Pageable pageable);
}
