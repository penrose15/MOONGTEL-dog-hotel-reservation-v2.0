package com.doghotel.reservation.reservation.controller;

import com.doghotel.reservation.domain.company.service.CompanyService;
import com.doghotel.reservation.domain.reservation.controller.CompanyReservationController;
import com.doghotel.reservation.domain.reservation.controller.CustomerReservationController;
import com.doghotel.reservation.domain.reservation.dto.*;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.service.ReservationService;
import com.doghotel.reservation.global.auth.WithAuthCustomer;
import com.doghotel.reservation.global.config.security.SecurityConfig;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
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
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = CustomerReservationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class CustomerReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private ReservationService reservationService;
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
    void registerReservationTest() throws Exception {
        Long postsId = 1L;
        String email = "customer@gmail.com";

        RegisterRoomInfoDto registerRoomInfoDto = new RegisterRoomInfoDto(1L, "small");
        RegisterRoomInfoCountDto registerRoomInfoCountDto = new RegisterRoomInfoCountDto(registerRoomInfoDto, 1);
        List<RegisterRoomInfoCountDto> registerRoomInfoCountDtos = List.of(registerRoomInfoCountDto);
        RegisterReservationDto registerReservationDto = new RegisterReservationDto(registerRoomInfoCountDtos, 1L, "2023-05-02", "2023-05-04");

        ReservationDto reservationDto = ReservationDto.builder()
                .dogList(List.of(1L))
                .roomId(1L)
                .dogCount(1)
                .checkInDate("2023-05-02")
                .checkOutDate("2023-05-04")
                .totalPrice(30000)
                .build();
        ReservationCreateDto reservationCreateDto  = ReservationCreateDto.builder()
                .reservationDtos(List.of(reservationDto))
                .totalCount(1)
                .totalPrice(30000)
                .customerEmail(email)
                .build();

        doReturn(reservationCreateDto)
                .when(reservationService).registerReservation(registerReservationDto, email, postsId);

        ResultActions actions = mockMvc.perform(
                post("/v1/customer/reservation/{postsId}", 1)
                        .header("Authorization", "Bearer + accessToken")
                        .header("Refresh", "Bearer + refreshToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(registerReservationDto))
                        .with(csrf())
        );
//
        actions
                .andExpect(status().isCreated())
                .andDo(document("registerReservation",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("accesstoken"),
                                headerWithName("Refresh").description("refreshtoken")
                        ),
                        pathParameters(
                                parameterWithName("postsId").description("posts 식별자")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("registerRoomInfoCountDtos").type(JsonFieldType.ARRAY).description("예약할 방에 대한 정보"),
                                        fieldWithPath("registerRoomInfoCountDtos[].roomInfoDto").type(JsonFieldType.OBJECT).description("예약할 방에 대한 정보"),
                                        fieldWithPath("registerRoomInfoCountDtos[].roomInfoDto.roomId").type(JsonFieldType.NUMBER).description("room 식별자"),
                                        fieldWithPath("registerRoomInfoCountDtos[].roomInfoDto.roomSize").type(JsonFieldType.STRING).description("room 사이즈"),
                                        fieldWithPath("registerRoomInfoCountDtos[].roomCount").type(JsonFieldType.NUMBER).description("방 개수"),
                                        fieldWithPath("postsId").type(JsonFieldType.NUMBER).description("post 식별자"),
                                        fieldWithPath("checkInDate").type(JsonFieldType.STRING).description("체크인 날짜"),
                                        fieldWithPath("checkOutDate").type(JsonFieldType.STRING).description("체크아웃 날짜")
                                )
                        )
//                        responseFields(
//                                List.of(
//                                        fieldWithPath("reservationDtos").type(JsonFieldType.ARRAY).description("예약 정보"),
//                                        fieldWithPath("reservationDtos[].checkInDate").type(JsonFieldType.STRING).description("체크인 날짜"),
//                                        fieldWithPath("reservationDtos[].checkOutDate").type(JsonFieldType.STRING).description("체크아웃 날짜"),
//                                        fieldWithPath("reservationDtos[].dogCount").type(JsonFieldType.NUMBER).description("강아지 수"),
//                                        fieldWithPath("reservationDtos[].roomId").type(JsonFieldType.NUMBER).description("room 식별자"),
//                                        fieldWithPath("reservationDtos[].totalPrice").type(JsonFieldType.NUMBER).description("총 가격"),
//                                        fieldWithPath("reservationDtos[].dogList").type(JsonFieldType.ARRAY).description("reservedDog 식별자"),
//                                        fieldWithPath("totalCount").type(JsonFieldType.NUMBER).description("예약할 방 총 개수"),
//                                        fieldWithPath("totalPrice").type(JsonFieldType.NUMBER).description("총 가격"),
//                                        fieldWithPath("customerEmail").type(JsonFieldType.STRING).description("사용자 이메일")
//                                )
//                        )
                        ));
    }
}
