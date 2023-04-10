package com.doghotel.reservation.reservation.controller;

import com.doghotel.reservation.domain.post.controller.PostsController;
import com.doghotel.reservation.domain.post.service.PostsService;
import com.doghotel.reservation.domain.reservation.controller.CompanyReservationController;
import com.doghotel.reservation.domain.reservation.dto.ReservationResponseDto;
import com.doghotel.reservation.domain.reservation.entity.Reservation;
import com.doghotel.reservation.domain.reservation.entity.Status;
import com.doghotel.reservation.domain.reservation.service.CompanyReservationService;
import com.doghotel.reservation.global.auth.WithAuthCompany;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.List;

import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = CompanyReservationController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class ReservationControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CompanyReservationService companyReservationService;
    @Autowired
    private Gson gson;

    @Test
    @WithAuthCompany
    void showReservationsTest() throws Exception {
        String email = "company@gmail.com";

        ReservationResponseDto response = new ReservationResponseDto(1L,
                LocalDate.of(2024,4,2),
                LocalDate.of(2024,4,5),
                2,
                40000,
                Status.ACCEPTED,
                1L,
                "company",
                1L,
                "roomSize");
        Pageable pageable = PageRequest.of(0,10, Sort.Direction.DESC, "reservationId");

        List<ReservationResponseDto> responseList = List.of(response);
        Page<ReservationResponseDto> responsePage = new PageImpl<>(responseList, pageable, 1L);

        doReturn(responsePage)
                .when(companyReservationService).showReservations(email, 0, 10);

        ResultActions actions = mockMvc.perform(
                get("/v1/company/reservation/list")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .header("Authorization","Bearer+ accesstoken")
                        .header("Refresh" , "Bearer+ refreshtoken")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
        );
        actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andDo(document("show-reservations",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("accesstoken"),
                                headerWithName("Refresh").description("refreshtoken")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("크기")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("response"),
                                        fieldWithPath("data[].reservationId").type(JsonFieldType.NUMBER).description("reservation 식별자"),
                                        fieldWithPath("data[].checkInDate").type(JsonFieldType.STRING).description("체크인 날짜"),
                                        fieldWithPath("data[].checkOutDate").type(JsonFieldType.STRING).description("체크 아웃 날짜"),
                                        fieldWithPath("data[].dogCount").type(JsonFieldType.NUMBER).description("강아지 숫자"),
                                        fieldWithPath("data[].totalPrice").type(JsonFieldType.NUMBER).description("총 가격"),
                                        fieldWithPath("data[].status").type(JsonFieldType.STRING).description("예약 상태"),
                                        fieldWithPath("data[].companyId").type(JsonFieldType.NUMBER).description("호텔 식별자"),
                                        fieldWithPath("data[].companyName").type(JsonFieldType.STRING).description("호텔 이름"),
                                        fieldWithPath("data[].roomId").type(JsonFieldType.NUMBER).description("방 식별자"),
                                        fieldWithPath("data[].roomSize").type(JsonFieldType.STRING).description("방 크기"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("page"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("size"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("totalElements"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("totalPages")
                                )
                        )
                        ));
    }

    @Test
    @WithAuthCompany
    void showReservationByStatus() throws Exception {
        String email = "company@gmail.com";

        ReservationResponseDto response1 = new ReservationResponseDto(1L,
                LocalDate.of(2024,4,2),
                LocalDate.of(2024,4,5),
                2,
                40000,
                Status.ACCEPTED,
                1L,
                "company",
                1L,
                "roomSize");
        ReservationResponseDto response2 = new ReservationResponseDto(1L,
                LocalDate.of(2024,4,2),
                LocalDate.of(2024,4,5),
                1,
                40000,
                Status.RESERVED,
                1L,
                "company",
                2L,
                "roomSize");
        Pageable pageable = PageRequest.of(0,10, Sort.Direction.DESC, "reservationId");

        List<ReservationResponseDto> responseList = List.of(response1, response2);
        Page<ReservationResponseDto> responsePage = new PageImpl<>(responseList, pageable, 1L);

        doReturn(responsePage)
                .when(companyReservationService).showReservationsByStatus(email, "ACCEPTED", 0, 10);

        ResultActions actions = mockMvc.perform(
                get("/v1/company/reservation/list/{status}", "ACCEPTED")
                        .queryParam("page", "1")
                        .queryParam("size", "10")
                        .header("Authorization", "Bearer + accessToken")
                        .header("Refresh", "Bearer + refreshToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isAccepted())
                .andDo(document("show-reservation-status",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("accesstoken"),
                                headerWithName("Refresh").description("refreshtoken")
                        ),
                        pathParameters(
                                parameterWithName("status").description("예약 상태 - RESERVED, ACCEPTED, VISITED, CANCELED")
                        ),
                        requestParameters(
                                parameterWithName("page").description("페이지"),
                                parameterWithName("size").description("크기")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("data").type(JsonFieldType.ARRAY).description("response"),
                                        fieldWithPath("data[].reservationId").type(JsonFieldType.NUMBER).description("reservation 식별자"),
                                        fieldWithPath("data[].checkInDate").type(JsonFieldType.STRING).description("체크인 날짜"),
                                        fieldWithPath("data[].checkOutDate").type(JsonFieldType.STRING).description("체크 아웃 날짜"),
                                        fieldWithPath("data[].dogCount").type(JsonFieldType.NUMBER).description("강아지 숫자"),
                                        fieldWithPath("data[].totalPrice").type(JsonFieldType.NUMBER).description("총 가격"),
                                        fieldWithPath("data[].status").type(JsonFieldType.STRING).description("예약 상태"),
                                        fieldWithPath("data[].companyId").type(JsonFieldType.NUMBER).description("호텔 식별자"),
                                        fieldWithPath("data[].companyName").type(JsonFieldType.STRING).description("호텔 이름"),
                                        fieldWithPath("data[].roomId").type(JsonFieldType.NUMBER).description("방 식별자"),
                                        fieldWithPath("data[].roomSize").type(JsonFieldType.STRING).description("방 크기"),
                                        fieldWithPath("pageInfo").type(JsonFieldType.OBJECT).description("페이지 정보"),
                                        fieldWithPath("pageInfo.page").type(JsonFieldType.NUMBER).description("page"),
                                        fieldWithPath("pageInfo.size").type(JsonFieldType.NUMBER).description("size"),
                                        fieldWithPath("pageInfo.totalElements").type(JsonFieldType.NUMBER).description("totalElements"),
                                        fieldWithPath("pageInfo.totalPages").type(JsonFieldType.NUMBER).description("totalPages")
                                )
                        )
                        ));
    }

//    @Test
//    @WithAuthCompany
//    void changeReservationStatus() throws Exception {
//        Long reservationId = 1L;
//        String status = "ACCEPTED";
//        String email = "company@gmail.com";
//
//        doNothing()
//                .when(companyReservationService).changeReservationStatus(reservationId, email, status);
//
//        ResultActions actions = mockMvc.perform(
//                patch("/v1/company/reservation/{reservation-id}",1L)
//                        .accept(MediaType.APPLICATION_JSON)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .header("Authorization", "Bearer + accessToken")
//                        .header("Refresh","Bearer + refreshtoken")
//                        .queryParam("status", status)
//        );
//
//        actions.andExpect(status().isAccepted())
//                .andDo(document("change-reservation-status",
//                        getDocumentRequest(),
//                        getDocumentResponse(),
//                        requestHeaders(
//                                headerWithName("Authorization").description("accesstoken"),
//                                headerWithName("Refresh").description("refreshtoken")
//                        ),
//                        pathParameters(
//                                parameterWithName("reservation-id").description("reservation 식별자")
//                        ),
//                        requestParameters(
//                                parameterWithName("status").description("예약 상태")
//                        )));
//    }
}
