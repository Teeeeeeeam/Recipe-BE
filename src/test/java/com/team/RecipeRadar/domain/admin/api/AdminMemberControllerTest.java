package com.team.RecipeRadar.domain.admin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.admin.application.AdminService;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.admin.dto.PostsCommentResponse;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.application.PostServiceImpl;
import com.team.RecipeRadar.global.email.listener.ResignEmailHandler;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockAdmin;
import com.team.mock.CustomMockUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(AdminMemberController.class)
class AdminMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @MockBean ApplicationEventPublisher eventPublisher;
    @MockBean ResignEmailHandler resignEmailHandler;
    @MockBean ApplicationEvent applicationEvent;
    @MockBean AdminService adminService;
    @MockBean MemberRepository memberRepository;
    @MockBean PostServiceImpl postService;
    @MockBean JwtProvider jwtProvider;
    @MockBean CustomOauth2Handler customOauth2Handler;
    @MockBean CustomOauth2Service customOauth2Service;
    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @CustomMockAdmin
    void getMembers_count() throws Exception {
        long count =10;
        given(adminService.searchAllMembers()).willReturn(count);


        mockMvc.perform(get("/api/admin/members/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(count));
    }

    @Test
    @CustomMockAdmin
    void getRecipe_count() throws Exception {
        long count =101111;
        given(adminService.searchAllRecipes()).willReturn(count);


        mockMvc.perform(get("/api/admin/recipes/count"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").value(count));
    }

    @Test
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
    @DisplayName("사용자 조회 API 무한 페이징 테스트")
    @CustomMockAdmin
    void getMemberAllInfo() throws Exception {

        String loginId = "testId";
        List<MemberDto>  memberDtoList = List.of(MemberDto.builder().username("회원1").email("email1").loginId(loginId).nickname("닉네임1").join_date(LocalDate.now()).build(),
                MemberDto.builder().username("회원2").email("email2").loginId("loginId2").nickname("닉네임2").join_date(LocalDate.now()).build());

        boolean hasNext = false;
        MemberInfoResponse memberInfoResponse = new MemberInfoResponse(memberDtoList, hasNext);
        given(adminService.memberInfos(isNull(),any())).willReturn(memberInfoResponse);

        mockMvc.perform(get("/api/admin/members/info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberInfos.[0].username").value("회원1"))
                .andExpect(jsonPath("$.data.memberInfos.[0].loginId").value(loginId))
                .andExpect(jsonPath("$.data.size()").value(2));
    }
    @Test
    @DisplayName("어드민 일괄 삭제 API 구현")
    @CustomMockAdmin
    void deleteAllUser() throws Exception {
        List<Long> list = List.of(1L, 2L, 3L);
        List<String> emails = List.of("example1@test.com");

        given(adminService.adminDeleteUsers(eq(list))).willReturn(emails);

        mockMvc.perform(delete("/api/admin/members")
                        .param("ids", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("삭제 성공"));

        verify(adminService, times(1)).adminDeleteUsers(anyList());
    }


    @Test
    @DisplayName("사용자 검색 API 무한 페이징 테스트")
    @CustomMockAdmin
    void getMemberSearchInfo() throws Exception {

        String loginId = "testId";
        String nickname = "nickName";
        List<MemberDto>  memberDtoList = List.of(MemberDto.builder().username("회원1").email("email1").loginId(loginId).nickname("닉네임1").join_date(LocalDate.now()).build(),
                MemberDto.builder().username("회원2").email("email2").loginId("loginId2").nickname(nickname).join_date(LocalDate.now()).build());

        boolean hasNext = false;
        MemberInfoResponse memberInfoResponse = new MemberInfoResponse(memberDtoList, hasNext);
        given(adminService.searchMember(eq(loginId),eq(nickname),any(),any(),isNull(),any())).willReturn(memberInfoResponse);

        mockMvc.perform(get("/api/admin/members/search?login-id="+loginId+"&nickname="+nickname))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberInfos.[0].username").value("회원1"))
                .andExpect(jsonPath("$.data.memberInfos.[0].loginId").value(loginId))
                .andExpect(jsonPath("$.data.size()").value(2));
    }

    @Test
    @DisplayName("어드민 게시글 일괄 삭제 API 구현")
    @CustomMockAdmin
    void deleteAllPosts() throws Exception {

        List<Long>  list= List.of(1l,2l,3l);
        doNothing().when(postService).deletePosts(anyList());

        mockMvc.perform(delete("/api/admin/posts")
                        .param("ids", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("게시글 삭제 성공"));
    }

    @Test
    @DisplayName("어드민 게시글 관련 댓글 조회")
    @CustomMockAdmin
    void postsContainsComment() throws Exception {
        Long post_id= 1l;
        List<CommentDto> commentDtoList = List.of(
                CommentDto.builder().comment_content("댓글1").create_at(LocalDateTime.now()).member(MemberDto.builder().loginId("testId").username("실명1").nickname("닉네임1").build()).build(),
                CommentDto.builder().comment_content("댓글2").create_at(LocalDateTime.now()).member(MemberDto.builder().loginId("testId1").username("실명2").nickname("닉네임2").build()).build()
        );
        PostsCommentResponse postsCommentResponse = new PostsCommentResponse(false, commentDtoList);
        given(adminService.getPostsComments(eq(post_id),isNull(),any())).willReturn(postsCommentResponse);

        mockMvc.perform(get("/api/admin/posts/comments?post-id="+post_id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data.comment.[0].member.nickname").value("닉네임1"))
                .andExpect(jsonPath("$.data.nextPage").value(false));

    }
    @Test
    @DisplayName("어드민 댓글 일괄 삭제 API 구현")
    @CustomMockAdmin
    void deleteAllComments() throws Exception {

        List<Long>  list= List.of(1l,2l,3l);
        doNothing().when(adminService).deleteComments(anyList());

        mockMvc.perform(delete("/api/admin/posts/comments?")
                        .param("ids", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("댓글 삭제 성공"));
    }

    @Test
    @DisplayName("어드민 API 요청시 권한이 어드민이 아닌 유저의 대해서 403 테스트")
    @CustomMockUser //일반 사용자 권한으로 접근
    void NoRoles_admin() throws Exception {

        List<Long>  list= List.of(1l,2l,3l);
        doNothing().when(adminService).deleteComments(anyList());

        mockMvc.perform(delete("/api/admin/posts/comments?")
                        .param("ids", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andExpect(status().is(403));
    }


    @Test
    @DisplayName("블랙리스트 이메일 임시 차단테스트")
    @CustomMockAdmin
    void unBlock() throws Exception {

        when(adminService.temporarilyUnblockUser(anyLong())).thenReturn(false);

        mockMvc.perform(post("/api/admin/blacklist/temporary-unblock/1"))
                .andDo(print())
                .andExpect(jsonPath("$.message").value("임시 차단 해제"));
    }
}