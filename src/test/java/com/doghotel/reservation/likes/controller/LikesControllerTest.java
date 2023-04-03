package com.doghotel.reservation.likes.controller;

import com.doghotel.reservation.domain.dog.controller.DogController;
import com.doghotel.reservation.domain.like.controller.LikesController;
import com.doghotel.reservation.domain.like.dto.LikeResponsesDto;
import com.doghotel.reservation.domain.like.service.LikesService;
import com.doghotel.reservation.global.auth.WithAuthCustomer;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.*;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = LikesController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class LikesControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LikesService likesService;

    @Autowired
    private Gson gson;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }

    @Test
    @WithAuthCustomer
    void likeTest() throws Exception {
        Long postsId = 1L;
        String email = "customer@gmail.com";
        doNothing()
                .when(likesService).changeLikes(email, postsId);

        ResultActions actions = mockMvc.perform(
                get("/v1/like/{posts-id}", postsId)
                        .header("Authorization", "Bearer + accessToken")
                        .header("Refresh", "Bearer + refreshToken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
        actions.andExpect(status().isOk())
                .andDo(document("changeLike",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("accesstoken"),
                                headerWithName("Refresh").description("refreshtoken")
                        ),
                        pathParameters(
                                parameterWithName("posts-id").description("posts 식별자")
                        )));
    }

    @Test
    @WithAuthCustomer
    void myLikes() throws Exception {
       String email = "customer@gmail.com";
        LikeResponsesDto like1 = LikeResponsesDto.builder()
                .likesId(1L)
                .postsId(1L)
                .title("posts title1")
                .build();
        LikeResponsesDto like2 = LikeResponsesDto.builder()
                .likesId(2L)
                .postsId(2L)
                .title("posts title2")
                .build();
        List<LikeResponsesDto> likes = List.of(like1, like2);
        doReturn(likes)
                .when(likesService).findMyLikes(email);

        ResultActions actions = mockMvc.perform(
                get("/v1/like/my-likes")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization","Bearer + accessToken")
                        .header("Refresh", "Bearer + refreshToken")
        );

        actions.andExpect(status().isOk())
                .andDo(document("myLikes",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("accesstoken"),
                                headerWithName("Refresh").description("refreshtoken")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("[].likesId").type(JsonFieldType.NUMBER).description("Like 식별자"),
                                        fieldWithPath("[].postsId").type(JsonFieldType.NUMBER).description("Posts 식별자"),
                                        fieldWithPath("[].title").type(JsonFieldType.STRING).description("게시물 제목")
                                )
                        )
                        ))
                .andDo(print());

    }
}
