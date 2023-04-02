package com.doghotel.reservation.customer.controller;

import com.doghotel.reservation.domain.customer.controller.CustomerController;
import com.doghotel.reservation.domain.customer.dto.CustomerProfileViewResponseDto;
import com.doghotel.reservation.domain.customer.dto.CustomerSignUpDto;
import com.doghotel.reservation.domain.customer.dto.CustomerUpdateRequestDto;
import com.doghotel.reservation.domain.customer.entity.Customer;
import com.doghotel.reservation.domain.customer.service.CustomerService;
import com.doghotel.reservation.global.auth.WithAuthCustomer;
import com.doghotel.reservation.global.config.security.SecurityConfig;
import com.doghotel.reservation.global.dto.EmailCheckDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.io.FileInputStream;
import java.util.List;

import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentRequest;
import static com.doghotel.reservation.global.util.ApiDocumentUtils.getDocumentResponse;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@WebMvcTest(value = CustomerController.class)
@MockBean(JpaMetamodelMappingContext.class)
@AutoConfigureRestDocs
public class CustomerControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private CustomerService customerService;
    @Autowired
    private Gson gson;

    @BeforeEach
    void setUp(WebApplicationContext webApplicationContext, RestDocumentationContextProvider restDocumentation) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(documentationConfiguration(restDocumentation))
                .build();
    }


    @WithAuthCustomer
    @Test
    void signUpCustomerTest() throws Exception {
        CustomerSignUpDto customerSignUpDto
                = CustomerSignUpDto.builder()
                .email("customer@gmail.com")
                .password("password1234!")
                .username("username")
                .phone("010-4915-7571")
                .build();
        String username = "username";

        doReturn(username)
                .when(customerService).signUpCustomer(customerSignUpDto);

        ResultActions actions = mockMvc.perform(
                post("/v1/customer/account")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(customerSignUpDto))
        );
        actions.andExpect(status().isCreated())
                .andDo(document("signup-customer",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(fieldWithPath("email").type(JsonFieldType.STRING).description("이메일"),
                                        fieldWithPath("password").type(JsonFieldType.STRING).description("패스워드"),
                                        fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 이름"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("핸드폰 번호")
                                ))));
    }

    @Test
    @WithAuthCustomer
    void updateCustomerTest() throws Exception {
        Long customerId = 1L;
        String email = "customer@gmail.com";
        CustomerUpdateRequestDto updateRequestDto
                = CustomerUpdateRequestDto.builder()
                .username("update username")
                .phone("010-1111-2222")
                .build();

        String username = "update username";
        doReturn(username)
                .when(customerService).updateCustomer(email, updateRequestDto, customerId);

        ResultActions actions = mockMvc.perform(
                patch("/v1/customer/account/{id}", 1)
                        .header("Authorization", "Bearer + accessToken")
                        .header("Refresh", "Bearer + accessToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(updateRequestDto))
                        .with(csrf())
        );

        actions.andExpect(status().isOk())
                .andDo(document("update-customer-info",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestHeaders(
                                headerWithName("Authorization").description("accessToken"),
                                headerWithName("Refresh").description("refreshToken")
                        ),
                        requestFields(
                                List.of(
                                        fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 이름"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("핸드폰 번호")
                                )
                        )));
    }

    @Test
    @WithAuthCustomer
    void updateCustomerProfileImageTest() throws Exception {
        String email = "customer@gmail.com";
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/test.png");

        String name = "image";
        String originalFilename = "test.png";

        MockMultipartFile file = new MockMultipartFile(name,originalFilename, "image/png",fileInputStream);

        doReturn(name)
                .when(customerService).updateCustomerProfile(email, file);

        ResultActions actions = mockMvc.perform(
                multipart("/v1/customer/profile")
                        .file("file", file.getBytes())
                        .header("Authorization", "Bearer + accessToken")
                        .header("Refresh", "Bearer refreshToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        actions.andExpect(status().isOk())
                .andDo(document("update-customer-profile-image",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestParts(partWithName("file").description("사용자 프로필 이미지")),
                        requestHeaders(
                                headerWithName("Authorization").description("accessToken"),
                                headerWithName("Refresh").description("refreshToken")
                        )));
    }

    @Test
    void checkDuplicateEmailTest() throws Exception {
        EmailCheckDto emailCheckDto = new EmailCheckDto("customer@gmail.com");
        doNothing()
                .when(customerService).verifyingEmail(emailCheckDto.email);

        ResultActions actions = mockMvc.perform(
                post("/v1/customer/email")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(gson.toJson(emailCheckDto))
        );

        actions.andExpect(status().isOk())
                .andDo(document("check-customer-email-is-duplicated",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        requestFields(
                                List.of(
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("이메일")
                                )
                        )));
    }

    @Test
    @WithAuthCustomer
    void showCustomerProfileTest() throws Exception {
        Long customerId = 1L;
        String email = "customer@gmail.com";
        CustomerProfileViewResponseDto customerProfileViewResponseDto = new CustomerProfileViewResponseDto("John", email,"010-1111-1111", "image","https://image.com");
        Customer customer = Customer.builder()
                .username("John")
                .email(email)
                .phone("010-1111-1111")
                .password("pwd1234!")
                .profile("image")
                .profileUrl("https://image.com")
                .build();

        when(customerService.getCustomerProfile(anyString(), anyLong()))
                .thenReturn(new CustomerProfileViewResponseDto("John", email,"010-1111-1111", "image","https://image.com"));

        ResultActions actions = mockMvc.perform(
                get("/v1/customer/profile/{id}", customerId)
                        .header("Authorization","Bearer + accessToken")
                        .header("Refresh","Bearer + refreshToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8")
        );

                actions.andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(customerProfileViewResponseDto.getUsername()))
                .andExpect(jsonPath("$.email").value(customerProfileViewResponseDto.getEmail()))
                .andExpect(jsonPath("$.phone").value(customerProfileViewResponseDto.getPhone()))
                .andExpect(jsonPath("$.profile").value(customerProfileViewResponseDto.getProfile()))
                .andExpect(jsonPath("$.profile_url").value(customerProfileViewResponseDto.getProfile_url()))
                .andDo(document("show-customer-profile",
                        getDocumentRequest(),
                        getDocumentResponse(),
                        pathParameters(
                                parameterWithName("id").description("사용자 식별자")
                        ),
                        requestHeaders(
                                headerWithName("Authorization").description("accessToken"),
                                headerWithName("Refresh").description("refreshToken")
                        ),
                        responseFields(
                                List.of(
                                        fieldWithPath("username").type(JsonFieldType.STRING).description("사용자 이름"),
                                        fieldWithPath("email").type(JsonFieldType.STRING).description("사용자 이메일"),
                                        fieldWithPath("phone").type(JsonFieldType.STRING).description("사용자 전화번호"),
                                        fieldWithPath("profile").type(JsonFieldType.STRING).description("사용자 프로필 사진 이름"),
                                        fieldWithPath("profile_url").type(JsonFieldType.STRING).description("사용자 프로필 사진 url")
                                )
                        )));
    }

}
