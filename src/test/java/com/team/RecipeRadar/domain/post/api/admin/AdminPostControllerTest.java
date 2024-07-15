package com.team.RecipeRadar.domain.post.api.admin;

import com.team.RecipeRadar.domain.post.application.admin.AdminPostService;
import com.team.RecipeRadar.domain.blackList.dto.response.PostsCommentResponse;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.application.user.PostServiceImpl;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.response.PostResponse;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.global.conig.SecurityTestConfig;
import com.team.RecipeRadar.domain.email.event.ResignEmailHandler;
import com.team.mock.CustomMockAdmin;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@Import(SecurityTestConfig.class)
@WebMvcTest(AdminPostController.class)
class AdminPostControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean ApplicationEventPublisher eventPublisher;
    @MockBean ResignEmailHandler resignEmailHandler;
    @MockBean ApplicationEvent applicationEvent;
    @MockBean AdminPostService adminService;
    @MockBean PostServiceImpl postService;

    @Test
    @DisplayName("게시글 수 전제 조회")
    @CustomMockAdmin
    void getPosts_count() throws Exception {
        long count =10;
        given(adminService.searchAllPosts()).willReturn(count);

        mockMvc.perform(get("/api/admin/posts/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(count));
    }


    @Test
    @DisplayName("어드민 댓글 일괄 삭제 API 구현")
    @CustomMockAdmin
    void deleteAllComments() throws Exception {

        List<Long>  list= List.of(1l,2l,3l);
        doNothing().when(adminService).deleteComments(anyList());

        mockMvc.perform(delete("/api/admin/posts/comments?")
                        .param("commentIds", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 삭제 성공"));
    }

    @Test
    @DisplayName("어드민 API 요청시 권한이 어드민이 아닌 유저의 대해서 403 테스트")
    @CustomMockUser//일반 사용자 권한으로 접근
    void NoRoles_admin() throws Exception {

        List<Long>  list= List.of(1l,2l,3l);
        doNothing().when(adminService).deleteComments(anyList());

        mockMvc.perform(delete("/api/admin/posts/comments?")
                        .param("commentIds", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andExpect(status().is(401));
    }




    @Test
    @DisplayName("어드민 게시글 일괄 삭제 API 구현")
    @CustomMockAdmin
    void deleteAllPosts() throws Exception {

        List<Long>  list= List.of(1l,2l,3l);
        doNothing().when(adminService).deletePosts(anyList());

        mockMvc.perform(delete("/api/admin/posts")
                        .param("postIds", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 삭제 성공"));
    }


}