package com.team.RecipeRadar.domain.blackList.api;

import com.team.RecipeRadar.domain.blackList.application.AdminBlackMemberService;
import com.team.RecipeRadar.domain.blackList.dto.BlackListDto;
import com.team.RecipeRadar.domain.blackList.dto.response.BlackListResponse;
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
import org.springframework.data.domain.Pageable;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

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
    @MockBean AdminBlackMemberService blackListService;
    @MockBean PostServiceImpl postService;
    @Test
    @DisplayName("블랙리스트 이메일 임시 차단테스트")
    @CustomMockAdmin
    void unBlock() throws Exception {

        when(blackListService.temporarilyUnblockUser(anyLong())).thenReturn(false);

        mockMvc.perform(post("/api/admin/blacklist/temporary-unblock/1"))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("임시 차단 유뮤"));
    }

    @Test
    @DisplayName("블랙리스트 이메일 조회")
    @CustomMockAdmin
    void getBlackList() throws Exception {

        BlackListDto blackListDto1 = new BlackListDto(1l, "test@email.com", true);
        BlackListDto blackListDto2 = new BlackListDto(2l, "test12@email.com", true);
        BlackListResponse blackListResponse = new BlackListResponse(true, List.of(blackListDto1, blackListDto2));
        when(blackListService.getBlackList(isNull(),any(Pageable.class))).thenReturn(blackListResponse);

        mockMvc.perform(get("/api/admin/black"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data.size()").value(2));
    }
    
    @Test
    @DisplayName("블랙 리스트 삭제")
    @CustomMockAdmin
    void deleteBlackList() throws Exception {

        doNothing().when(blackListService).deleteBlackList(anyLong());

        mockMvc.perform(delete("/api/admin/blacklist/"+1l))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("삭제 성공"));

        verify(blackListService, times(1)).deleteBlackList(anyLong());

    }

    @Test
    @DisplayName("블랙리스트 해당 이메일 조회")
    @CustomMockAdmin
    void searchEmailByBlackList() throws Exception {

        BlackListDto blackListDto1 = new BlackListDto(1l, "test@email.com", false);
        BlackListResponse blackListResponse = new BlackListResponse(true, List.of(blackListDto1));

        when(blackListService.searchEmailBlackList(eq("test@email.com"),isNull(),any(Pageable.class))).thenReturn(blackListResponse);

        mockMvc.perform(get("/api/admin/black/search")
                        .param("email","test@email.com"))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data.nextPage").value(true))
                .andExpect(jsonPath("$.data.blackList.size()").value(1));
    }


}