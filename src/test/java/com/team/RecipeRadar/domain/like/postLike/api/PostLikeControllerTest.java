package com.team.RecipeRadar.domain.like.postLike.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.like.postLike.application.PostLikeServiceImpl;
import com.team.RecipeRadar.domain.like.postLike.dto.PostLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.global.jwt.JwtProvider;
import com.team.RecipeRadar.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@WebMvcTest(PostLikeController.class)
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
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("좋아요 성공"));
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
                .andExpect(jsonPath("$.message").value("좋아요 해제"));
    }
}