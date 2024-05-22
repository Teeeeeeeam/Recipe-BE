package com.team.RecipeRadar.domain.admin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.admin.application.AdminService;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockAdmin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminMemberController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean AdminService adminService;
    @MockBean MemberRepository memberRepository;
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
        given(adminService.memberInfos(any())).willReturn(memberInfoResponse);

        mockMvc.perform(get("/api/admin/members/info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberInfos.[0].username").value("회원1"))
                .andExpect(jsonPath("$.data.memberInfos.[0].loginId").value(loginId))
                .andExpect(jsonPath("$.data.size()").value(2));
    }
    
    @Test
    @DisplayName("어드민 삭제 API 구현")
    @CustomMockAdmin
    void deleteUser() throws Exception {
        Long memberId = 1l;

        doNothing().when(adminService).adminDeleteUser(anyLong());

        mockMvc.perform(delete("/api/admin/member/"+memberId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("삭제 성공"));
    }

    @Test
    @DisplayName("어드민 삭제 API 구현시 회원 없을때 예외")
    @CustomMockAdmin
    void deleteUser_thr() throws Exception {
        Long memberId = 1l;

        doThrow(new NoSuchElementException("사용자를 찾을수 업습니다.")).when(adminService).adminDeleteUser(anyLong());

        mockMvc.perform(delete("/api/admin/member/"+memberId))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("사용자를 찾을수 없습니다."));
    }

    @Test
    @DisplayName("어드민 일괄 삭제 API 구현")
    @CustomMockAdmin
    void deleteAllUser() throws Exception {

        List<Long>  list= List.of(1l,2l,3l);
        doNothing().when(adminService).adminDeleteUsers(anyList());

        mockMvc.perform(delete("/api/admin/members?")
                .param("ids", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("삭제 성공"));
    }

}