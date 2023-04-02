package com.doghotel.reservation.dog.controller;

import com.doghotel.reservation.domain.customer.controller.CustomerController;
import com.doghotel.reservation.domain.dog.controller.DogController;
import com.doghotel.reservation.domain.dog.dto.DogPostDto;
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
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
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
        FileInputStream fileInputStream = new FileInputStream("src/test/resources/test.png");

        String name = "image";
        String originalFilename = "test.png";

        MockMultipartFile multipart = new MockMultipartFile(name,originalFilename, "image/png",fileInputStream);

        DogPostDto dogPostDto = DogPostDto.builder()
                .dogName("happy")
                .type("Golden Retriever")
                .gender("MALE")
                .age(3)
                .weight(29.3)
                .etc("any special notes")
                .build();
        String email = "customer@gmail.com";

        doReturn("happy")
                .when(dogService).addDogs(dogPostDto, multipart, email);

        Part dto = new MockPart("dto", gson.toJson(dogPostDto).getBytes(StandardCharsets.UTF_8));
        Part file = new MockPart("file", multipart.getBytes());

        ResultActions actions = mockMvc.perform(
                multipart("/v1/dog")
                        .file(multipart)
                        .content(gson.toJson(dogPostDto))
                        .header("Authorization", "Bearer + accessToken")
                        .header("Refresh", "Bearer + refreshToken")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf())

        );


        actions.andExpect(status().isCreated())
                .andDo(print());
    }
}
