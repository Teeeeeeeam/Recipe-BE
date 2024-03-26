package com.team.RecipeRadar.domain.like.postLike.api;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.like.postLike.application.PostLikeServiceImpl;
import com.team.RecipeRadar.domain.like.postLike.dto.PostLikeDto;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Date;

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

    @Value("${security.token}")
    private String key;

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
                .andExpect(jsonPath("$.success").value(true))
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
                .andDo(print());

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
}
