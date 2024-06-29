package com.team.RecipeRadar.domain.qna.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.qna.application.user.QnAService;
import com.team.RecipeRadar.domain.qna.domain.AnswerType;
import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionRequest;
import com.team.RecipeRadar.domain.qna.dto.response.QuestionAllResponse;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityTestConfig.class)
@WebMvcTest(QnAController.class)
class QnAControllerTest {

    @MockBean QnAService qnAService;
    @Autowired MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Test
    @DisplayName("문의사항 작성 테스트")
    void question() throws Exception {
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestionContent("내용");
        questionRequest.setQuestionType(QuestionType.ACCOUNT_INQUIRY);
        questionRequest.setTitle("제목");
        questionRequest.setAnswer(AnswerType.NONE);

        doNothing().when(qnAService).accountQuestion(questionRequest,null);

        MockMultipartFile file = new MockMultipartFile("questionRequest", null, "application/json", objectMapper.writeValueAsString(questionRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/question")
                .file(file)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("비로그인 문의 사항 등록"));
    }


    @Test
    @DisplayName("사용자 문의사항 작성 테스트")
    @CustomMockUser
    void question_user() throws Exception {
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestionContent("내용");
        questionRequest.setQuestionType(QuestionType.GENERAL_INQUIRY);
        questionRequest.setTitle("제목");
        questionRequest.setAnswer(AnswerType.NONE);

        doNothing().when(qnAService).accountQuestion(questionRequest,null);

        MockMultipartFile file = new MockMultipartFile("questionRequest", null, "application/json", objectMapper.writeValueAsString(questionRequest).getBytes(StandardCharsets.UTF_8));

        mockMvc.perform(multipart("/api/user/question")
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("문의 사항 등록"));
    }

    @Test
    @DisplayName("문의사항 전체 조회")
    @CustomMockUser
    void qnaAll() throws Exception {
        List<QuestionDto> questionDtoList = List.of(
                QuestionDto.builder().id(1l).title("제목1").questionType(QuestionType.GENERAL_INQUIRY).answerType(AnswerType.NONE).status(QuestionStatus.PENDING).build(),
                QuestionDto.builder().id(2l).title("제목2").questionType(QuestionType.GENERAL_INQUIRY).answerType(AnswerType.NONE).status(QuestionStatus.PENDING).build()
        );
        QuestionAllResponse questionAllResponse = new QuestionAllResponse(true, questionDtoList);

        when(qnAService.allUserQuestion(isNull(),anyLong(),isNull(),isNull(), any(Pageable.class))).thenReturn(questionAllResponse);

        mockMvc.perform(get("/api/user/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.questions.size()").value(2))
                .andExpect(jsonPath("$.data.nextPage").value(true));
    }
    
    @Test
    @DisplayName("문의사항 삭제")
    @CustomMockUser
    void deleteQuestion() throws Exception {
        doNothing().when(qnAService).deleteQuestions(anyList(),anyLong());

        mockMvc.perform(delete("/api/user/questions?questionIds=1,2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("삭제 성공"));
    }
    
    @Test
    @DisplayName("상세 조회")
    @CustomMockUser
    void detailQnA() throws Exception {
        QuestionDto questionDto = QuestionDto.builder().status(QuestionStatus.COMPLETED).build();

        when(qnAService.viewResponse(any(),anyLong())).thenReturn(questionDto);

        mockMvc.perform(get("/api/user/question/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));
    }
}