package com.team.RecipeRadar.domain.like.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.like.application.PostLikeServiceImpl;
import com.team.RecipeRadar.domain.like.dto.PostLikeDto;
import com.team.RecipeRadar.domain.like.dto.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
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

    @MockBean
    private PostLikeServiceImpl postLikeService;
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
    @CustomMockUser
    @DisplayName("좋아요 컨트롤러 테스트")
    void add_like_Test() throws Exception {

        PostLikeDto postLikeDto = PostLikeDto.builder().postId(1l).memberId(2l).build();
        given(postLikeService.addLike(postLikeDto)).willReturn(true);

        mockMvc.perform(post("/api/user/postLike")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(postLikeDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("좋아요 해제"));
    }
    
    @Test
    @CustomMockUser
    @DisplayName("좋아요 해제하기 테스트")
    void delete_like_test()throws Exception{
        
        PostLikeDto postLikeDto = PostLikeDto.builder().postId(1l).memberId(2l).build();
        given(postLikeService.addLike(postLikeDto)).willReturn(false);

        mockMvc.perform(post("/api/user/postLike")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(postLikeDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("좋아요 성공"));
    }

    @Test
    @DisplayName("좋아요 목록테스트")
    void get_likes() throws Exception {

        given(postLikeService.checkLike(null,1l)).willReturn(true);

        mockMvc.perform(get("/api/likeCheck")
                        .param("postId","1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("좋아요 상태"));

    }

    @Test
    @DisplayName("jwt 토큰 테스트")
    void get_lsikes() throws Exception {

        String sign = JWT.create()
                .withClaim("id", "testId")
                .withSubject("subject")
                .withExpiresAt(new Date()).sign(Algorithm.HMAC512("test"));

        given(postLikeService.checkLike(anyString(), anyLong())).willReturn(true);

        mockMvc.perform(get("/api/likeCheck")
                        .param("postId", "1")
                        .header("Authorization", "Bearer " + sign)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 응답 상태코드가 200 OK인지 확인/
                .andDo(print()); // 테스트 결과 출력
    }

    @Test
    @DisplayName("사용자페이지- 좋아요한 게시글의 대한 페이징 성공시")
    @CustomMockUser
    public void getUserLike_page_success() throws Exception {
        String loginId = "test";
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        List<UserLikeDto> userLikeDtos = new ArrayList<>();
        userLikeDtos.add(new UserLikeDto(1L, "내용", "제목"));
        userLikeDtos.add(new UserLikeDto(2L, "내용1", "제목1"));

        UserInfoLikeResponse response = UserInfoLikeResponse.builder()
                .nextPage(true)
                .content(userLikeDtos)
                .build();

        given(postLikeService.getUserLikesByPage(anyString(), anyString(), isNull(),any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/user/info/{login-id}/posts/likes", loginId).cookie(cookie))
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
        String loginId = "test";
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        List<UserLikeDto> userLikeDtos = new ArrayList<>();
        userLikeDtos.add(new UserLikeDto(1L, "내용", "제목"));
        userLikeDtos.add(new UserLikeDto(2L, "내용1", "제목1"));

        given(postLikeService.getUserLikesByPage(anyString(), anyString(),isNull(), any(Pageable.class))).willThrow(new NoSuchElementException("접근 할 수 없는 페이지입니다."));

        mockMvc.perform(get("/api/user/info/{login-id}/posts/likes", loginId).cookie(cookie))
                .andExpect(status().isBadRequest())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("접근 할 수 없는 페이지입니다."));
    }
    
}
