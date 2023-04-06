package com.doghotel.reservation.post.repository;

import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.post.dto.PostsResponsesDto;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.entity.PostsImg;
import com.doghotel.reservation.domain.post.repository.PostsImgRepository;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import com.doghotel.reservation.domain.post.repository.PostsRepositoryImpl;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.repository.RoomRepository;
import com.doghotel.reservation.global.querydsl.QueryDslConfig;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.persistence.EntityManager;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Import({QueryDslConfig.class})
public class PostsRepositoryTest {
    @Autowired
    private PostsRepositoryImpl postsRepositoryImpl;
    @Autowired
    private PostsRepository postsRepository;
    @Autowired
    private PostsImgRepository postsImgRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private CompanyRepository companyRepository;
    @Autowired
    EntityManager em;
    JPAQueryFactory queryFactory;

    @BeforeEach
    public void init() {
        queryFactory = new JPAQueryFactory(em);
    }

    @AfterEach
    public void deleteAll() {
        roomRepository.deleteAll();
        postsImgRepository.deleteAll();
        postsRepository.deleteAll();
        companyRepository.deleteAll();
    }

    private void setInit() {
        Company company = Company.builder()
                .email("company@gmail.com")
                .password("1234abcd!")
                .companyName("test company")
                .address("test address")
                .detailAddress("test detailAddress")
                .representativeNumber("123456789")
                .build();
        company = companyRepository.save(company);
        Posts posts = Posts.builder()
                .title("title posts")
                .content("content")
                .latitude("1111111")
                .longitude("2222222")
                .address("address")
                .company(company)
                .build();
        postsRepository.save(posts);
        Company company1 = Company.builder()
                .email("company1@gmail.com")
                .password("1234abcd!")
                .companyName("test company1")
                .address("test address1")
                .detailAddress("test detailAddress1")
                .representativeNumber("1234567891")
                .build();
        company1 = companyRepository.save(company1);
        Posts posts1 = Posts.builder()
                .title("title posts1")
                .content("content1")
                .latitude("1111111")
                .longitude("2222222")
                .address("address1")
                .company(company1)
                .build();
        postsRepository.save(posts1);
        PostsImg postsImg = PostsImg.builder()
                .originalFilename("test.png")
                .name("image")
                .url("https://abc.com")
                .posts(posts)
                .build();
        postsImgRepository.save(postsImg);
        Room room = Room.builder()
                .price(10000)
                .roomCount(10)
                .roomSize("for small dog")
                .company(company)
                .build();
        roomRepository.save(room);
        PostsImg postsImg1 = PostsImg.builder()
                .originalFilename("test.png")
                .name("image")
                .url("https://abc.com")
                .posts(posts1)
                .build();
        postsImgRepository.save(postsImg1);
        Room room1 = Room.builder()
                .price(10000)
                .roomCount(10)
                .roomSize("for small dog")
                .company(company1)
                .build();
        roomRepository.save(room1);
    }

    @Test
    void getMainPages() {
        //given
        setInit();
        PostsResponsesDto response = new PostsResponsesDto(1L,1L,"image", "https://abc.com", "title posts1", 10000);
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        List<PostsResponsesDto> list = List.of(response);

        //when
        Page<PostsResponsesDto> mainPages = postsRepositoryImpl.getMainPages(pageable);
        List<PostsResponsesDto> result = mainPages.getContent();
        System.out.println("### " + postsRepository.findAll().size());
        //then
        System.out.println(result);
        assertThat(result.size())
                .isEqualTo(2);

    }

    @Test
    void searchPagesByTitleOrContentOrAddressTest() {
        setInit();
        PostsResponsesDto response = new PostsResponsesDto(1L,1L,"image", "https://abc.com", "title",  10000);
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        List<PostsResponsesDto> list = List.of(response);

        Page<PostsResponsesDto> postsResponsesDtos = postsRepositoryImpl.searchPagesByTitleOrContentOrAddress("title", pageable);
        List<PostsResponsesDto> result = postsResponsesDtos.getContent();
        System.out.println(result.size());

        assertThat(result.get(0).getTitle())
                .contains("title");
    }
}
