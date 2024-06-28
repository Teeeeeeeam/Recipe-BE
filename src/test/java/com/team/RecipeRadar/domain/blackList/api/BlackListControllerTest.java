package com.team.RecipeRadar.domain.blackList.api;

import com.team.RecipeRadar.domain.blackList.application.AdminBlackMemberService;
import com.team.RecipeRadar.domain.post.application.user.PostServiceImpl;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.RecipeRadar.domain.email.event.ResignEmailHandler;
import com.team.mock.CustomMockAdmin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Import(SecurityTestConfig.class)
@WebMvcTest(BlackListController.class)
class BlackListControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean ApplicationEventPublisher eventPublisher;
    @MockBean ResignEmailHandler resignEmailHandler;
    @MockBean ApplicationEvent applicationEvent;
    @MockBean AdminBlackMemberService adminService;
    @MockBean PostServiceImpl postService;
    @Test
    @DisplayName("블랙리스트 이메일 임시 차단테스트")
    @CustomMockAdmin
    void unBlock() throws Exception {

        when(adminService.temporarilyUnblockUser(anyLong())).thenReturn(false);

        mockMvc.perform(post("/api/admin/blacklist/temporary-unblock/1"))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("임시 차단 유뮤"));
    }

}