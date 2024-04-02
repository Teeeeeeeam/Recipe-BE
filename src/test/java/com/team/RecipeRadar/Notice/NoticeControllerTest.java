package com.team.RecipeRadar.Notice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notice.dto.AddNoticeRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@AutoConfigureMockMvc
public class NoticeControllerTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext context;

    @Autowired
    private NoticeRepository noticeRepository;

    @BeforeEach
    public void mockMvcSetup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .build();
    }
    @AfterEach
    public void cleanUp() {
        noticeRepository.deleteAll();
    }

    @DisplayName("공지사항 글 추가 테스트")
    @Test
    public void addNotice() throws Exception {
        final String url = "/api/admin/notices";
        final String noticeTitle = "noticeTitle";
        final String noticeContent = "noticeContent";
        final AddNoticeRequest userRequest = new AddNoticeRequest(noticeTitle,noticeContent);

        final String requestBody = objectMapper.writeValueAsString(userRequest);
    }
}