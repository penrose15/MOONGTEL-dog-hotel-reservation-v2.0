package com.doghotel.reservation.company.controller;

import com.doghotel.reservation.domain.company.controller.CompanyController;
import com.doghotel.reservation.domain.company.dto.CompanyResponseDto;
import com.doghotel.reservation.domain.company.dto.CompanySignUpDto;
import com.doghotel.reservation.domain.company.dto.CompanyUpdateDto;
import com.doghotel.reservation.domain.company.service.CompanyService;
import com.doghotel.reservation.global.auth.WithAuthCompany;
import com.doghotel.reservation.global.config.security.SecurityConfig;
import com.google.gson.Gson;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.io.FileInputStream;
import java.util.List;

import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = CompanyController.class,
        excludeFilters = {
                @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
        })
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class CompanyControllerTest {
        @Autowired
        private MockMvc mockMvc;
        @MockBean
        private CompanyService companyService;
        @Autowired
        private Gson gson;

        @Test
        @WithAuthCompany
        void signUpCompanyTest() throws Exception {
                String companyName = "test company name";
                CompanySignUpDto companySignUpDto = CompanySignUpDto.builder()
                        .email("company@gmail.com")
                        .password("password1234!")
                        .representativeNumber("123456789")
                        .companyName("test company name")
                        .address("서울특별시 송파구 올림픽로 240")
                        .detailAddress("20층")
                        .build();
                doReturn(companyName)
                        .when(companyService).companySignUp(companySignUpDto);

                ResultActions actions = mockMvc.perform(
                        post("/v1/company/account")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(gson.toJson(companySignUpDto))
                );
                actions.andExpect(status().isCreated())
                        .andDo(document("signup-company",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestFields(
                                        List.of(fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                                fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                                                fieldWithPath("representativeNumber").type(JsonFieldType.STRING).description("사업자 등록 번호"),
                                                fieldWithPath("companyName").type(JsonFieldType.STRING).description("회사 이름(호텔 이름)"),
                                                fieldWithPath("address").type(JsonFieldType.STRING).description("회사 주소"),
                                                fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소"))
                                )));
        }

        @Test
        @WithAuthCompany
        void updateCompanyTest() throws Exception {
                String updateCompanyName = "update company name";
                String email = "company@gmail.com";
                CompanyUpdateDto companyUpdateDto = CompanyUpdateDto.builder()
                        .companyName("update company name")
                        .representativeNumber("11223344")
                        .address("서울특별시 강남구 영동대로 513")
                        .detailAddress("2층")
                        .build();
                Long companyId = 1L;
                doReturn(updateCompanyName)
                        .when(companyService).updateCompany(companyUpdateDto, email, companyId);

                ResultActions actions = mockMvc.perform(
                        patch("/v1/company/{company-id}",1L)
                                .header("Authorization", "Bearer (accessToken)")
                                .header("Refresh", "Bearer (refreshToken)")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())
                                .content(gson.toJson(companyUpdateDto))
                );
                actions.andExpect(status().isOk())
                        .andDo(document("update-company",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(headerWithName("Authorization").description("access token"),
                                        headerWithName("Refresh").description("refresh token")),
                                pathParameters(parameterWithName("company-id").description("회사 식별자")),
                                requestFields(
                                        List.of(
                                                fieldWithPath("companyName").type(JsonFieldType.STRING).description("회사 이름 혹은 호텔 이름"),
                                                fieldWithPath("representativeNumber").type(JsonFieldType.STRING).description("사업자 등록 번호"),
                                                fieldWithPath("address").type(JsonFieldType.STRING).description("회사 주소"),
                                                fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("상세 주소")
                                        )
                                )));
        }

        @Test
        @WithAuthCompany
        void updateCompanyImgTest() throws Exception {
                Long companyId = 1L;
                String email = "company@gmail.com";
                FileInputStream fileInputStream = new FileInputStream("src/test/resources/test.png");

                String name = "image";
                String originalFilename = "test.png";

                MockMultipartFile file = new MockMultipartFile(name,originalFilename, "image/png",fileInputStream);

                doReturn(name)
                        .when(companyService).updateCompanyImg(email, file);

                ResultActions actions = mockMvc.perform(
                        MockMvcRequestBuilders.multipart("/v1/company/profile-image",HttpMethod.POST)
                                .file("file",file.getBytes())
                                .header("Authorization", "Bearer + accessToken")
                                .header("Refresh", "Bearer refreshToken")
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON)
                                .with(csrf())

                );
                actions.andExpect(status().isOk())
                        .andDo(document("update-company-profile",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestParts(partWithName("file").description("회사 프로필 업로드")),
                                requestHeaders(
                                        headerWithName("Authorization").description("access token"),
                                        headerWithName("Refresh").description("refresh token"))));
        }

        @Test
        @WithAuthCompany
        void profileCompanyTest() throws Exception {
                String email = "company@gmail.com";
                CompanyResponseDto companyResponseDto = new CompanyResponseDto("company",
                        "company@gmail.com", "address", "detailAddress", "11223344", "filename", "https://image.com");

                doReturn(companyResponseDto)
                        .when(companyService).getCompany(anyString());

                ResultActions actions = mockMvc.perform(get("/v1/company/profile")
                        .header("Authorization", "Bearer + accessToken")
                        .header("Refresh", "Bearer + refreshToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON));

                actions.andExpect(status().isOk())
                        .andExpect(jsonPath("$.companyName").value(companyResponseDto.getCompanyName()))
                        .andExpect(jsonPath("$.email").value(companyResponseDto.getEmail()))
                        .andExpect(jsonPath("$.address").value(companyResponseDto.getAddress()))
                        .andExpect(jsonPath("$.detailAddress").value(companyResponseDto.getDetailAddress()))
                        .andExpect(jsonPath("$.representativeNumber").value(companyResponseDto.getRepresentativeNumber()))
                        .andExpect(jsonPath("$.filename").value(companyResponseDto.getFilename()))
                        .andExpect(jsonPath("$.imgUrl").value(companyResponseDto.getImgUrl()))
                        .andDo(document("get-company-profile",
                                getDocumentRequest(),
                                getDocumentResponse(),
                                requestHeaders(
                                        headerWithName("Authorization").description("access token"),
                                        headerWithName("Refresh").description("refresh token")),
                                responseFields(
                                        List.of(
                                                fieldWithPath("companyName").type(JsonFieldType.STRING).description("회사 이름"),
                                                fieldWithPath("email").type(JsonFieldType.STRING).description("회사 이메일"),
                                                fieldWithPath("address").type(JsonFieldType.STRING).description("회사 주소"),
                                                fieldWithPath("detailAddress").type(JsonFieldType.STRING).description("회사 상세 주소"),
                                                fieldWithPath("representativeNumber").type(JsonFieldType.STRING).description("회사 사업장 등록 번호"),
                                                fieldWithPath("filename").type(JsonFieldType.STRING).description("회사 프로필 사진 이름"),
                                                fieldWithPath("imgUrl").type(JsonFieldType.STRING).description("프로필 사진 url")
                                        )
                                )
                        ));


        }
}
