package com.team.RecipeRadar.domain.qna.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.qna.application.user.QnAServiceImpl;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionRequest;
import com.team.RecipeRadar.global.conig.TestConfig;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestConfig.class)
@WebMvcTest(QnAController.class)
class QuestionControllerTest {

    @MockBean private QnAServiceImpl questionService;
    @Autowired private MockMvc mockMvc;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("사용자 계정 관련 질문 등록 성공")
    void accountQuestion_Success() throws Exception {
        QuestionRequest questionRequest = new QuestionRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.TEXT_PLAIN_VALUE, "fileContent".getBytes());
        MockMultipartFile userAddRequestMultipartFile = new MockMultipartFile("questionRequest", null, "application/json", objectMapper.writeValueAsString(questionRequest).getBytes(StandardCharsets.UTF_8));

        // when
        mockMvc.perform(multipart("/api/question")
                        .file(file)
                        .file(userAddRequestMultipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비로그인 문의 사항 등록"));
    }

    @Test
    @CustomMockUser
    @DisplayName("로그인한 사용자들의 일반 문의 등록 성공")
    void generalQuestion_Success() throws Exception {
        QuestionRequest questionRequest = new QuestionRequest();
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", MediaType.TEXT_PLAIN_VALUE, "fileContent".getBytes());
        MockMultipartFile userAddRequestMultipartFile = new MockMultipartFile("questionRequest", null, "application/json", objectMapper.writeValueAsString(questionRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/user/question")
                        .file(file)
                        .file(userAddRequestMultipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("문의 사항 등록"));
    }

}

