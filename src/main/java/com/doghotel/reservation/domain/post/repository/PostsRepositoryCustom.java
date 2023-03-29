package com.doghotel.reservation.domain.post.repository;

import com.doghotel.reservation.domain.post.dto.PostsResponsesDto;
import com.doghotel.reservation.domain.post.entity.Posts;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PostsRepositoryCustom {
    Page<PostsResponsesDto> getMainPages(Pageable pageable);
    Page<PostsResponsesDto> searchPagesByTitleOrContentOrAddress(String keyword,Pageable pageable);



}
