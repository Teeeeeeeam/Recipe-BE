package com.team.RecipeRadar.domain.questions.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.questions.application.QuestionServiceImpl;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionRequest;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockAdmin;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(QuestionController.class)
class QuestionControllerTest {

    @MockBean
    private QuestionServiceImpl questionService;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MemberRepository memberRepository;
    @MockBean
    JwtProvider jwtProvider;
    @MockBean
    CustomOauth2Handler customOauth2Handler;
    @MockBean
    CustomOauth2Service customOauth2Service;

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

    @Test
    @DisplayName("어드민의 상세조회 성공")
    @CustomMockAdmin
    void details_Question_Success() throws Exception {
        Long questionId = 1L;

        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionContent("컨튼트");
        questionDto.setAnswerEmail("test@example.com");
        
        when(questionService.detailAdminQuestion(eq(questionId), anyLong())).thenReturn(questionDto);

        mockMvc.perform(get("/api/admin/question/{id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist());
    }
}

