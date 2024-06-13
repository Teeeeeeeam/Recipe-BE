package com.team.RecipeRadar.domain.like.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.like.application.PostLikeServiceImpl;
import com.team.RecipeRadar.domain.like.dto.PostLikeRequest;
import com.team.RecipeRadar.domain.like.dto.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.userInfo.utils.CookieUtils;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockUser;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;
import java.util.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(PostLikeController.class)
@Slf4j
class PostLikeControllerTest {

    @MockBean private PostLikeServiceImpl postLikeService;
    @Autowired private MockMvc mockMvc;

    @MockBean CookieUtils cookieUtils;
    @MockBean MemberRepository memberRepository;
    @MockBean JwtProvider jwtProvider;
    @MockBean CustomOauth2Handler customOauth2Handler;
    @MockBean CustomOauth2Service customOauth2Service;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @CustomMockUser
    @DisplayName("좋아요 컨트롤러 테스트")
    void add_like_Test() throws Exception {

        Long memberId = 2l;
        PostLikeRequest postLikeRequest = PostLikeRequest.builder().postId(1l).build();
        given(postLikeService.addLike(postLikeRequest,memberId)).willReturn(true);

        mockMvc.perform(post("/api/user/posts/like")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postLikeRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("좋아요 성공"));
    }
    
    @Test
    @CustomMockUser
    @DisplayName("좋아요 해제하기 테스트")
    void delete_like_test()throws Exception{
        Long memberId = 2l;
        PostLikeRequest postLikeRequest = PostLikeRequest.builder().postId(1l).build();
        given(postLikeService.addLike(postLikeRequest,memberId)).willReturn(false);

        mockMvc.perform(post("/api/user/posts/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postLikeRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("좋아요 성공"));
    }

    @Test
    @CustomMockUser
    @DisplayName("좋아요 목록테스트")
    void get_likes() throws Exception {

        given(postLikeService.checkLike(null,1l)).willReturn(true);

        mockMvc.perform(get("/api/user/posts/1/like/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("좋아요 상태"));

    }

    @Test
    @CustomMockUser
    @DisplayName("좋아요 상태 테스트")
    void get_lsikes() throws Exception {

        given(postLikeService.checkLike(anyLong(), anyLong())).willReturn(true);

        mockMvc.perform(get("/api/user/posts/1/like/check")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 응답 상태코드가 200 OK인지 확인/
                .andDo(print()); // 테스트 결과 출력
    }

    @Test
    @DisplayName("사용자페이지- 좋아요한 게시글의 대한 페이징 성공시")
    @CustomMockUser
    public void getUserLike_page_success() throws Exception {
        Long memberId = 1l;
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        List<UserLikeDto> userLikeDtos = new ArrayList<>();
        userLikeDtos.add(new UserLikeDto(1L, 1l,"내용", "제목"));
        userLikeDtos.add(new UserLikeDto(2L, 1l,"내용1", "제목1"));

        UserInfoLikeResponse response = UserInfoLikeResponse.builder()
                .nextPage(true)
                .content(userLikeDtos)
                .build();

        given(cookieUtils.validCookie(anyString(),anyString())).willReturn(true);
        given(postLikeService.getUserLikesByPage(eq(memberId),isNull(),any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/user/info/posts/likes")
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회 성공"))
                .andExpect(jsonPath("$.data.nextPage").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(2))
                .andDo(print());
    }

    @Test
    @DisplayName("사용자페이지- 좋아요한 게시글의 대한 페이징 실패시")
    @CustomMockUser
    public void getUserLike_page_fail() throws Exception {
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        List<UserLikeDto> userLikeDtos = new ArrayList<>();
        userLikeDtos.add(new UserLikeDto(1L, 1l,"내용", "제목"));
        userLikeDtos.add(new UserLikeDto(2L, 1l,"내용1", "제목1"));

        given(cookieUtils.validCookie(eq("login-id"),anyString())).willReturn(false);

        mockMvc.perform(get("/api/user/info/posts/likes").cookie(cookie))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("올바르지 않은 접근입니다."));
    }
    
}
