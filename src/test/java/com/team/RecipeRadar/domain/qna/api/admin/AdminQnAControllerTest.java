package com.team.RecipeRadar.domain.qna.api.admin;

import com.team.RecipeRadar.domain.qna.application.admin.AdminQnAServiceImpl;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.global.conig.TestConfig;
import com.team.mock.CustomMockAdmin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestConfig.class)
@WebMvcTest(AdminQnAController.class)
class AdminQnAControllerTest {

    @MockBean private AdminQnAServiceImpl adminQnAService;
    @Autowired private MockMvc mockMvc;

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
}

