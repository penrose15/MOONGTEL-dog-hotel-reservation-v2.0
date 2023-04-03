package com.doghotel.reservation.post.controller;

import com.doghotel.reservation.domain.dog.controller.DogController;
import com.doghotel.reservation.domain.post.controller.PostsController;
import com.doghotel.reservation.domain.post.dto.PostsDto;
import com.doghotel.reservation.domain.post.dto.PostsResponsesDto;
import com.doghotel.reservation.domain.post.dto.PostsUpdateDto;
import com.doghotel.reservation.domain.post.service.PostsService;
import com.doghotel.reservation.global.auth.WithAuthCompany;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.http.Part;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = PostsController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private PostsService postsService;
    @Autowired
    private Gson gson;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @WithAuthCompany
    void createPostsTest() throws Exception {
        PostsDto postsDto = PostsDto.builder()
                .title("title")
                .content("content")
                .phoneNumber("010-1111-2222")
                .longitude("123456789")
                .latitude("123456789")
                .checkInStartTime("오전 11:00")
                .checkInEndTime("오후 11:00")
                .tagList(List.of("tag1","tag2"))
                .build();

        MockMultipartFile files
                = new MockMultipartFile("files","files","image/png", new byte[]{1,2,3,4});

        doReturn("title")
                .when(postsService).createPosts(anyString(),any(PostsDto.class),anyList());

        MockPart part = new MockPart("dto", gson.toJson(postsDto).getBytes(StandardCharsets.UTF_8));
        part.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        ResultActions actions = mockMvc
                .perform(multipart("/v1/post")
                        .file(files)
                        .part(part)
                        .header("Authorization","Bearer+ accesstoken")
                        .header("Refresh" , "refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON));

        actions.andExpect(status().isCreated())
                .andDo(document("create-post",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("accesstoken"),
                                headerWithName("Refresh").description("refreshtoken")
                        ),
                        requestParts(
                                List.of(
                                        partWithName("dto").description("post dto"),
                                        partWithName("files").description("이미지 사진들")
                                )
                        )));
    }

    @Test
    @WithAuthCompany
    void updatePostsTest() throws Exception {
        Long postsId = 1L;
        PostsUpdateDto updateDto = PostsUpdateDto.builder()
                .title("title")
                .content("content")
                .latitude("123456789")
                .longitude("123456789")
                .phoneNumber("010-1111-2222")
                .checkInStartTime("오전 11:00")
                .checkInEndTime("오후 11:00")
                .address("서울특별시 강남구 강남대로 1번지")
                .tagList(List.of("tag1","tag2"))
                .build();
        MockMultipartFile file
                = new MockMultipartFile("file","file","image/png", new byte[]{1,2,3,4});
        String email = "customer@gmail.com";
        String result = "title";

        MockPart dto = new MockPart("dto","dto",gson.toJson(updateDto).getBytes(StandardCharsets.UTF_8));
        dto.getHeaders().setContentType(MediaType.APPLICATION_JSON);

        doReturn(result)
                .when(postsService).updatePosts(email, postsId, updateDto, List.of(file));

        ResultActions actions = mockMvc.perform(
                multipart("/v1/post/{post-id}", 1L)
                        .part(dto)
                        .file(file)
                        .header("Authorization", "Bearer + accessTokne")
                        .header("Refresh", "Bearer + refreshtoken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andDo(document("update-post",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("post-id").description("post 식별자")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("accesstoken"),
                                headerWithName("Refresh").description("refreshtoken")
                        ),
                        requestParts(
                                List.of(
                                        partWithName("dto").description("post dto"),
                                        partWithName("file").description("이미지 사진들")
                                )
                        )));
    }

    @Test
    @WithAuthCompany
    void showPostsTest() {
        PostsResponsesDto postsResponsesDto1 = new PostsResponsesDto(
               1L, 1L, "image","https://image.com","title", 0.5,50000
        );
        PostsResponsesDto postsResponsesDto2 = new PostsResponsesDto(
                2L, 2L, "image","https://image.com","title", 0.5,50000
        );
        List<PostsResponsesDto> postsResponsesDtos = List.of(postsResponsesDto1,postsResponsesDto2);
        Pageable pageable = PageRequest.of(0, 10, Sort.Direction.DESC,"id");
        

    }
}
