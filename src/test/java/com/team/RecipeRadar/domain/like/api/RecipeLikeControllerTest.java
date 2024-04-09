package com.team.RecipeRadar.domain.like.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.like.api.RecipeLikeController;
import com.team.RecipeRadar.domain.like.application.RecipeLikeServiceImpl;
import com.team.RecipeRadar.domain.like.dto.RecipeLikeDto;
import com.team.RecipeRadar.domain.like.dto.UserInfoLikeResponse;
import com.team.RecipeRadar.domain.like.dto.UserLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(RecipeLikeController.class)
@ExtendWith(SpringExtension.class)
class RecipeLikeControllerTest {

    @MockBean
    private RecipeLikeServiceImpl recipeLikeService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberRepository memberRepository;
    @MockBean
    JwtProvider jwtProvider;
    @MockBean
    CustomOauth2Handler customOauth2Handler;
    @MockBean
    CustomOauth2Service customOauth2Service;


    @Test
    @CustomMockUser
    @DisplayName("좋아요 해제하기")
    void add_like_recipe() throws Exception {
        RecipeLikeDto recipeLikeDto = RecipeLikeDto.builder().recipeId(1l).memberId(2l).build();

        given(recipeLikeService.addLike(recipeLikeDto)).willReturn(true);

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

        RecipeLikeDto recipeLikeDto = RecipeLikeDto.builder().recipeId(1l).memberId(2l).build();
        given(recipeLikeService.addLike(recipeLikeDto)).willReturn(false);

        mockMvc.perform(post("/api/user/recipe/like")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(recipeLikeDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("좋아요 성공"));
    }

    @Test
    @DisplayName("좋아요 목록 테스트")
    void get_likes() throws Exception {

        given(recipeLikeService.checkLike(null,1l)).willReturn(true);

        mockMvc.perform(get("/api/recipe/like/check/{recipe-id}", 123L)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print());

    }

    @Test
    @DisplayName("레시피 좋아요 테스트")
    void getLikes() throws Exception {
        // JWT 토큰 생성
        String sign = JWT.create()
                .withClaim("id", "testId")
                .withSubject("subject")
                .withExpiresAt(new Date())
                .sign(Algorithm.HMAC512("test"));

        // recipeLikeService.checkLike 메서드가 호출될 때 true를 반환하도록 설정
        given(recipeLikeService.checkLike(anyString(), anyLong())).willReturn(true);

        // GET 요청 수행 및 테스트
        mockMvc.perform(get("/api/recipe/like/check/{recipe-id}", 123L) // recipe-id를 적절한 값으로 변경
                        .header("Authorization", "Bearer " + sign)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // 응답 상태코드가 200 OK인지 확인
                .andDo(print()); // 테스트 결과 출력
    }

}