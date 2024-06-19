package com.team.RecipeRadar.domain.like.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.like.application.RecipeLikeServiceImpl;
import com.team.RecipeRadar.domain.like.dto.like.RecipeLikeRequest;
import com.team.RecipeRadar.domain.like.dto.like.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.dto.like.UserLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.global.utils.CookieUtils;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.application.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.application.CustomOauth2Service;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.Disabled;
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
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeLikeController.class)
@ExtendWith(SpringExtension.class)
class RecipeLikeControllerTest {

    @MockBean private RecipeLikeServiceImpl recipeLikeService;
    @Autowired private MockMvc mockMvc;
    @Autowired ObjectMapper objectMapper;

    @MockBean MemberRepository memberRepository;
    @MockBean JwtProvider jwtProvider;
    @MockBean CustomOauth2Handler customOauth2Handler;
    @MockBean CustomOauth2Service customOauth2Service;
    @MockBean CookieUtils cookieUtils;


    @Test
    @CustomMockUser
    @DisplayName("좋아요 해제하기")
    void add_like_recipe() throws Exception {
        Long memberId = 1l;
        RecipeLikeRequest recipeLikeDto = RecipeLikeRequest.builder().recipeId(1l).build();

        given(recipeLikeService.addLike(recipeLikeDto,memberId)).willReturn(true);

        mockMvc.perform(post("/api/user/recipe/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeLikeDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("좋아요 해제"));
    }

    @Test
    @CustomMockUser
    @DisplayName("좋아요 해제하기 테스트")
    void delete_like_test()throws Exception{
        Long memberId = 2l;

        RecipeLikeRequest recipeLikeDto = RecipeLikeRequest.builder().recipeId(1l).build();
        given(recipeLikeService.addLike(recipeLikeDto,memberId)).willReturn(false);

        mockMvc.perform(post("/api/user/recipe/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeLikeDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("좋아요 성공"));
    }

    @Test
    @CustomMockUser
    @DisplayName("좋아요 목록 테스트")
    void get_likes() throws Exception {

        Long memberId = 1l;
        Long recipeId = 123l;
        given(recipeLikeService.checkLike(eq(memberId),eq(recipeId))).willReturn(false);

        mockMvc.perform(get("/api/user/recipe/{recipeId}/like/check", recipeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("좋아요 상태"));

    }

    @Test
    @CustomMockUser
    @DisplayName("레시피 좋아요 테스트")
    void getLikes() throws Exception {

        Long memberId = 1l;
        Long recipeId = 123l;

        given(recipeLikeService.checkLike(eq(memberId),eq(recipeId))).willReturn(true);
        // GET 요청 수행 및 테스트
        mockMvc.perform(get("/api/user/recipe/{recipeId}/like/check/", recipeId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자페이지- 좋아요한 레시피의 대한 페이징 성공시")
    @CustomMockUser
    public void getUserLike_page_success() throws Exception {
        Long memberId = 1l;
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        List<UserLikeDto> userLikeDtos = new ArrayList<>();
        userLikeDtos.add(new UserLikeDto(1L, 1l,"내용", "제목"));
        userLikeDtos.add(new UserLikeDto(2L, 1l,"내용1", "제목1"));

        doNothing().when(cookieUtils).validCookie(anyString(),anyString());
        UserInfoLikeResponse response = UserInfoLikeResponse.builder()
                .nextPage(true)
                .content(userLikeDtos)
                .build();

        given(recipeLikeService.getUserLikesByPage(eq(memberId), isNull(),any(Pageable.class))).willReturn(response);

        mockMvc.perform(get("/api/user/info/recipe/likes")
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
    @Disabled
    @DisplayName("사용자페이지- 좋아요한 레시피의 대한 페이징 실패시")
    @CustomMockUser
    public void getUserLike_page_fail() throws Exception {
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        List<UserLikeDto> userLikeDtos = new ArrayList<>();
        userLikeDtos.add(new UserLikeDto(1L, 1l,"내용", "제목"));
        userLikeDtos.add(new UserLikeDto(2L, 1l,"내용1", "제목1"));

        doNothing().when(cookieUtils).validCookie(anyString(),anyString());

        mockMvc.perform(get("/api/user/info/recipe/likes")
                        .cookie(cookie))
                .andExpect(status().isForbidden())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("올바르지 않은 접근입니다."));
    }
}