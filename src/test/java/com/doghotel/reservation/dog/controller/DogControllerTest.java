package com.doghotel.reservation.dog.controller;

import com.doghotel.reservation.domain.customer.controller.CustomerController;
import com.doghotel.reservation.domain.dog.controller.DogController;
import com.doghotel.reservation.domain.dog.dto.*;
import com.doghotel.reservation.domain.dog.service.DogImageService;
import com.doghotel.reservation.domain.dog.service.DogService;
import com.doghotel.reservation.global.auth.WithAuthCustomer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = DogController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class DogControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private DogService dogService;
    @MockBean
    private DogImageService dogImageService;
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
    void addDogTest() throws Exception {
        String email = "customer@gmail.com";

        String name = "image";
        String originalFilename = "test.png";
        String url = "https://image.com";

        DogImageResponseDto dto = DogImageResponseDto.builder()
                .originalFilename(originalFilename)
                .fileName(name)
                .url(url)
                .build();

        List<DogImageResponseDto> dtos = List.of(dto);
        DogPostDto dogPostDto = DogPostDto.builder()
                .dogName("happy")
                .type("말티즈")
                .gender("MALE")
                .age(4)
                .weight(3.6)
                .etc("중성화 수술")
                .build();
        DogPostRequestDto dogPostRequestDto = new DogPostRequestDto(dogPostDto, dtos);

        doReturn("save dog")
                .when(dogService).addDogs(dogPostRequestDto, email);

        ResultActions actions = mockMvc.perform(
                post("/v1/dog")
                        .header("Authorization", "Bearer + access token")
                        .header("Refresh", "Bearer + refresh token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(dogPostRequestDto))
        );

        actions.andExpect(status().isCreated())
                .andDo(document("add-dog",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("accessToken"),
                                headerWithName("Refresh").description("refreshToken")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("dogPostDto").type(JsonFieldType.OBJECT).description(""),
                                        fieldWithPath("dogPostDto.dogName").type(JsonFieldType.STRING).description("강아지 이름"),
                                        fieldWithPath("dogPostDto.type").type(JsonFieldType.STRING).description("강아지 종"),
                                        fieldWithPath("dogPostDto.gender").type(JsonFieldType.STRING).description("강아지 성별"),
                                        fieldWithPath("dogPostDto.age").type(JsonFieldType.NUMBER).description("강아지 나이"),
                                        fieldWithPath("dogPostDto.weight").type(JsonFieldType.NUMBER).description("강아지 무게"),
                                        fieldWithPath("dogPostDto.etc").type(JsonFieldType.STRING).description("강아지 특이 사항"),
                                        fieldWithPath("dogImageResponseDtos").type(JsonFieldType.ARRAY).description("강아지 이미지 사진들"),
                                        fieldWithPath("dogImageResponseDtos[].originalFilename").type(JsonFieldType.STRING).description("강아지 이미지 원본 사진이름"),
                                        fieldWithPath("dogImageResponseDtos[].fileName").type(JsonFieldType.STRING).description("강아지 이미지 사진 이름"),
                                        fieldWithPath("dogImageResponseDtos[].url").type(JsonFieldType.STRING).description("강아지 이미지 url")
                                )
                        )
                        ));
    }

    @Test
    @WithAuthCustomer
    void updateDog() throws Exception {
        Long dogId = 1L;
        String email = "customer@gmail.com";
        DogUpdateDto dogUpdateDto = DogUpdateDto.builder()
                .dogName("happy")
                .type("푸들")
                .gender("FEMALE")
                .age(3)
                .weight(7.6)
                .etc("중성화 수술")
                .build();

        doReturn("happy")
                .when(dogService).updateDog(dogId, dogUpdateDto, email);

        ResultActions actions = mockMvc.perform(
                patch("/v1/dog/{dog-id}", 1L)
                        .header("Authorization", "Bearer + access token")
                        .header("Refresh", "Bearer + refresh token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(dogUpdateDto))
        );

        actions.andExpect(status().isOk())
                .andDo(document("dog-update",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("accessToken"),
                                headerWithName("Refresh").description("refreshToken")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("dogName").type(JsonFieldType.STRING).description("강아지 이름"),
                                        fieldWithPath("type").type(JsonFieldType.STRING).description("강아지 종"),
                                        fieldWithPath("gender").type(JsonFieldType.STRING).description("강아지 성별"),
                                        fieldWithPath("age").type(JsonFieldType.NUMBER).description("강아지 나이"),
                                        fieldWithPath("weight").type(JsonFieldType.NUMBER).description("강아지 무게"),
                                        fieldWithPath("etc").type(JsonFieldType.STRING).description("강아지 특이 사항")
                                )
                        )));
    }

    @Test
    @WithAuthCustomer
    void showDogByDogIdTest() throws Exception {
        Long dogId = 1L;
        String email = "customer@gmail.com";

        DogResponseDto dog = DogResponseDto.builder()
                .dogId(dogId)
                .dogName("happy")
                .type("푸들")
                .gender("FEMALE")
                .age(4)
                .weight(7.3)
                .etc("중성화 수술")
                .build();

        String name = "image";
        String originalFilename = "test.png";
        String url = "https://image.com";

        DogImageResponseDto image = DogImageResponseDto.builder()
                .originalFilename(originalFilename)
                .fileName(name)
                .url(url)
                .build();

        List<DogImageResponseDto> images = List.of(image);

        DogDetailProfileDto dogDetailProfileDto = new DogDetailProfileDto(dog, images);

        doReturn(dog)
                .when(dogService).showDogByDogId(dogId, email);
        doReturn(images)
                .when(dogImageService).findDogImages(dogId);

        ResultActions actions = mockMvc.perform(
                get("/v1/dog/{dog-id}",dogId)
                        .header("Authorization", "Bearer + access token")
                        .header("Refresh", "Bearer + refresh token")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.dogResponseDto.dogId").value(dog.getDogId()))
                .andDo(document("show-dog-by-dog-id",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("accessToken"),
                                headerWithName("Refresh").description("refreshToken")
                        ),
                        pathParameters(
                                parameterWithName("dog-id").description("강아지 식별자")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("dogResponseDto.dogId").type(JsonFieldType.NUMBER).description("강아지 식별자"),
                                        fieldWithPath("dogResponseDto.dogName").type(JsonFieldType.STRING).description("강아지 이름"),
                                        fieldWithPath("dogResponseDto.type").type(JsonFieldType.STRING).description("강아지 종"),
                                        fieldWithPath("dogResponseDto.gender").type(JsonFieldType.STRING).description("강아지 성별"),
                                        fieldWithPath("dogResponseDto.age").type(JsonFieldType.NUMBER).description("강아지 나이"),
                                        fieldWithPath("dogResponseDto.weight").type(JsonFieldType.NUMBER).description("강아지 무게"),
                                        fieldWithPath("dogResponseDto.etc").type(JsonFieldType.STRING).description("강아지 특이사항"),
                                        fieldWithPath("dogImageResponseDtoList").type(JsonFieldType.ARRAY).description("강아지 이미지 사진들"),
                                        fieldWithPath("dogImageResponseDtoList[].originalFilename").type(JsonFieldType.STRING).description("강아지 이미지 원본 사진이름"),
                                        fieldWithPath("dogImageResponseDtoList[].fileName").type(JsonFieldType.STRING).description("강아지 이미지 사진 이름"),
                                        fieldWithPath("dogImageResponseDtoList[].url").type(JsonFieldType.STRING).description("강아지 이미지 url")
                                )
                        )));
    }



}
