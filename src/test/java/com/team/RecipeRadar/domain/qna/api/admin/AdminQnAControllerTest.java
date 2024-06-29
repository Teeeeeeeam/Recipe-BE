package com.team.RecipeRadar.domain.qna.api.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.qna.application.admin.AdminQnAServiceImpl;
import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.qna.dto.response.QuestionAllResponse;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.mock.CustomMockAdmin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(SecurityTestConfig.class)
@WebMvcTest(AdminQnAController.class)
class AdminQnAControllerTest {

    @MockBean private AdminQnAServiceImpl adminQnAService;
    @Autowired private MockMvc mockMvc;

    private final ObjectMapper objectMapper= new ObjectMapper();

    @Test
    @DisplayName("어드민의 상세조회 성공")
    @CustomMockAdmin
    void details_Question_Success() throws Exception {
        Long questionId = 1L;

        QuestionDto questionDto = new QuestionDto();
        questionDto.setQuestionContent("컨튼트");
        questionDto.setAnswerEmail("test@example.com");
        
        when(adminQnAService.detailAdminQuestion(eq(questionId), anyLong())).thenReturn(questionDto);

        mockMvc.perform(get("/api/admin/question/{id}", questionId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").doesNotExist());
    }

    @Test
    @DisplayName("문의사항 전제 조회")
    @CustomMockAdmin
    void allQnA() throws Exception {
        List<QuestionDto> questionDtoList = List.of(
                QuestionDto.builder().title("문의1").build(),
                QuestionDto.builder().title("문의2").build(),
                QuestionDto.builder().title("문의3").build(),
                QuestionDto.builder().title("문의4").build()
        );

        QuestionAllResponse questionAllResponse = new QuestionAllResponse(false, questionDtoList);
        when(adminQnAService.allQuestion(isNull(),isNull(),isNull(),any(Pageable.class))).thenReturn(questionAllResponse);

        mockMvc.perform(get("/api/admin/questions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nextPage").value(false))
                .andExpect(jsonPath("$.data.questions.size()").value(4));
    }
    @Test
    @DisplayName("문의사항 답변")
    @CustomMockAdmin
    void answerQnA() throws Exception {

        QuestionAnswerRequest questionAnswerRequest = new QuestionAnswerRequest();
        questionAnswerRequest.setAnswerContent("답변내용");
        questionAnswerRequest.setQuestionStatus(QuestionStatus.COMPLETED);
        questionAnswerRequest.setAnswerTitle("답변 제목");

        doNothing().when(adminQnAService).questionAnswer(anyLong(),any(),anyString());

        mockMvc.perform(post("/api/admin/questions/1/answers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(questionAnswerRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("답변 작성 성공"));
    }
}

