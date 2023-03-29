package com.doghotel.reservation.post.service;


import com.doghotel.reservation.domain.company.entity.Company;
import com.doghotel.reservation.domain.company.repository.CompanyRepository;
import com.doghotel.reservation.domain.company.service.CompanyVerifyService;
import com.doghotel.reservation.domain.post.dto.*;
import com.doghotel.reservation.domain.post.entity.Posts;
import com.doghotel.reservation.domain.post.entity.PostsImg;
import com.doghotel.reservation.domain.post.repository.PostsRepository;
import com.doghotel.reservation.domain.post.repository.PostsRepositoryImpl;
import com.doghotel.reservation.domain.post.service.PostsImgService;
import com.doghotel.reservation.domain.post.service.PostsService;
import com.doghotel.reservation.domain.room.dto.RoomResponseDto;
import com.doghotel.reservation.domain.room.entity.Room;
import com.doghotel.reservation.domain.room.service.RoomService;
import com.doghotel.reservation.domain.tag.service.TagService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
public class PostsServiceTest {
    @Mock
    private PostsRepository postsRepository;
    @Mock
    private PostsRepositoryImpl postsRepositoryImpl;
    @Mock
    private CompanyVerifyService verifyService;
    @Mock
    private PostsImgService postsImgService;
    @Mock
    private RoomService roomService;
    @Mock
    private TagService tagService;

    @InjectMocks
    private PostsService postsService;
    private PostsDto postsDto;
    private String email;
    private Posts posts;
    private PostsImg postsImg;
    private List<PostsImgDto> postsImgDtos;
    private List<MultipartFile> multipartFiles;
    private List<PostsImg> postsImgs;
    private Company company;
    private List<RoomResponseDto> roomResponseDtos;
    private List<Room> roomList;

    private void setInit() throws IOException {
        postsDto = PostsDto.builder()
                .title("title")
                .content("content")
                .address("address")
                .checkInStartTime("오전 11:00")
                .checkInEndTime("오후 11:00")
                .latitude("111111")
                .longitude("1111111")
                .phoneNumber("010-1111-2222")
                .tagList(List.of("산책","인스타감성"))
                .build();
        email = "company@gmail.com";
        company = Company.builder()
                .email("company@gmail.com")
                .password("1234abcd!")
                .companyName("test company")
                .address("test address")
                .detailAddress("test detailAddress")
                .representativeNumber("123456789")
                .build();

        FileInputStream fileInputStream = new FileInputStream("src/test/resources/test.png");

        String name = "image";
        String originalFilename = "test.png";

        String url = "https://s3.com";

        MultipartFile file = new MockMultipartFile(name,
                originalFilename,
                "png",
                fileInputStream);
        multipartFiles = List.of(file);
        posts = postsDto.toEntity();
        postsImg = PostsImg.builder()
                .originalFilename(originalFilename)
                .name(name)
                .url(url)
                .posts(posts)
                .build();
        postsImgs = List.of(postsImg);
        PostsImgDto postsImgDto = PostsImgDto.builder()
                .postsImgId(1L)
                .filename(name)
                .url(url)
                .build();
        postsImgDtos = List.of(postsImgDto);
        posts.designateCompany(company);

        RoomResponseDto roomResponseDto = RoomResponseDto.builder()
                .roomId(1L)
                .roomSize("small")
                .price(100000)
                .build();
        roomResponseDtos = List.of(roomResponseDto);

        Room room = Room.builder()
                .price(100000)
                .roomSize("small")
                .roomCount(10)
                .company(company)
                .build();
        roomList = List.of(room);
    }

    @Test
    void createPostsTest() throws IOException {
        //given
        setInit();

        //when
        doReturn(company)
                .when(verifyService).verifyingEmail(email);
        doReturn(posts)
                .when(postsRepository).save(any(Posts.class));
        doReturn(postsImgs)
                .when(postsImgService).savePostsImg(multipartFiles, posts);
        doReturn("save tags")
                .when(tagService).createTag(postsDto.getTagList(), posts);

        //then
        String title = postsService.createPosts(email, postsDto, multipartFiles);

        assertThat(title)
                .isEqualTo("title");
    }

    @Test
    void updatePostsTest() throws IOException {
        //given
        setInit();
        Long postsId = 1L;
        PostsUpdateDto postsUpdateDto = PostsUpdateDto.builder()
                .title("update title")
                .content("update content")
                .latitude("222222222")
                .longitude("22222222222")
                .address("update address")
                .phoneNumber("010-1111-6666")
                .checkInStartTime("오전 11:00")
                .checkInEndTime("오후 11:00")
                .tagList(List.of("업데이트"))
                .build();
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/test.png");

        String name = "updateImage";
        String originalFilename = "test.png";

        String url = "https://updateImg.com";

        MultipartFile file = new MockMultipartFile(name,
                originalFilename,
                "png",
                fileInputStream);
        List<MultipartFile> updateFiles = List.of(file);

        //when
        doReturn(company)
                .when(verifyService).verifyingEmail(email);
        doReturn(Optional.of(posts))
                .when(postsRepository).findById(postsId);
        doReturn(postsImgs)
                .when(postsImgService).updatePostsImg(updateFiles, posts);
        doReturn("update tag")
                .when(tagService).updateTags(postsUpdateDto.getTagList(), posts);

        //then
        String updatedTitle = postsService.updatePosts(email, postsId, postsUpdateDto, updateFiles);

        assertThat(updatedTitle)
                .isEqualTo("update title");
    }

    @Test
    void getPostTest() throws IOException {
        //given
        Long postsId = 1L;
        Long companyId = 1L;
        setInit();
        posts = Posts.builder()
                .title("title")
                .content("content")
                .latitude("latitude")
                .longitude("longitude")
                .address("address")
                .phoneNumber("010-1111-2222")
                .checkInStartTime(LocalTime.of(11,0))
                .checkInEndTime(LocalTime.of(23, 0))
                .company(company)
                .score(0.0)
                .postsImgs(postsImgs)
                .build();

        List<String> tags = List.of("tag1","tag2");
        //when
        doReturn(Optional.ofNullable(posts))
                .when(postsRepository).findById(postsId);
        doReturn(postsImgDtos)
                .when(postsImgService).findPostsImgById(postsId);
        doReturn(tags)
                .when(tagService).findTagsByPostsId(postsId);
        doReturn(roomResponseDtos)
                .when(roomService).findByCompanyId(anyLong());

        //then
        PostsResponseDto response = postsService.getPost(postsId);

        assertThat(response.getTitle())
                .isEqualTo("title");
        assertThat(response.getContent())
                .isEqualTo("content");
        assertThat(response.getScore())
                .isEqualTo(0);

    }

    @Test
    void getMainPages() {
        //given
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC, "id");
        PostsResponsesDto postsResponsesDto
                = new PostsResponsesDto(1L, 1L, "filename", "https://url.com", "title", 0.0, 100000);
        List<PostsResponsesDto> postsResponsesDtoList = List.of(postsResponsesDto);
        Page<PostsResponsesDto> postsResponsesDtos = new PageImpl<>(postsResponsesDtoList, pageable, 1L);

        //when
        doReturn(postsResponsesDtos)
                .when(postsRepositoryImpl).getMainPages(pageable);

        //then
        Page<PostsResponsesDto> postsResponsesDtoPage = postsService.getMainPages(0, 10);
        List<PostsResponsesDto> responses = postsResponsesDtoPage.getContent();

        assertThat(responses.get(0).getTitle())
                .isEqualTo("title");
        assertThat(postsResponsesDtoPage.getTotalPages())
                .isEqualTo(1);
        assertThat(postsResponsesDtoPage.getTotalElements())
                .isEqualTo(1);
    }

    @Test
    void deletePostsTest() {
        //given
        Long postsId = 1L;
        //when
        doReturn(company)
                .when(verifyService).verifyingEmail(email);
        doNothing()
                .when(postsRepository).deleteById(postsId);
        //then
        postsService.deletePosts(email, postsId);
    }

}
