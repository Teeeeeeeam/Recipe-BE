package com.team.RecipeRadar.domain.post.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.post.application.PostServiceImpl;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostRequest;
import com.team.RecipeRadar.domain.post.dto.info.UserInfoPostResponse;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostController.class)
class PostControllerTest {

    @MockBean
    private PostServiceImpl postService;
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
    @DisplayName("사용자가 작성한 게시글 목록 조회 API 성공 테스트")
    @CustomMockUser
    void postTitlePage_success() throws Exception {

        Cookie cookie = new Cookie("login-id", "fakeCookie");
        String loginId= "test";

        List<UserInfoPostRequest > requests = new ArrayList<>();
        requests.add(new UserInfoPostRequest(2l,"타이틀1"));
        requests.add(new UserInfoPostRequest(3l,"타이틀2"));

        UserInfoPostResponse infoPostResponse = UserInfoPostResponse.builder()
                .nextPage(false)
                .content(requests).build();

        given(postService.userPostPage(anyString(),anyString(),any(Pageable.class))).willReturn(infoPostResponse);

        mockMvc.perform(get("/api/user/info/{login-id}/posts",loginId)
                .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data.nextPage").value(false))
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andExpect(jsonPath("$.data.content.[0].id").value(2))
                .andExpect(jsonPath("$.data.content.[0].postTitle").value("타이틀1"))
                .andExpect(jsonPath("$.data.content.[1].id").value(3))
                .andExpect(jsonPath("$.data.content.[1].postTitle").value("타이틀2"));


    }

    @Test
    @DisplayName("사용자가 작성한 게시글 목록 조회 API 실패 테스트")
    @CustomMockUser
    void postTitlePage_fail() throws Exception {

        String loginId= "test";
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        given(postService.userPostPage(anyString(),anyString(),any(Pageable.class))).willThrow(new AccessDeniedException("접근 할수 없는 페이지 입니다."));

        mockMvc.perform(get("/api/user/info/{login-id}/posts",loginId)
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("접근 할수 없는 페이지 입니다."));
    }
}