package com.team.RecipeRadar.domain.admin.api;

import com.team.RecipeRadar.domain.admin.application.blackMember.AdminBlackMemberService;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.application.PostServiceImpl;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.listener.ResignEmailHandler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.mock.CustomMockAdmin;
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
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(BlackListMemberController.class)
class BlackListMemberControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean ApplicationEventPublisher eventPublisher;
    @MockBean ResignEmailHandler resignEmailHandler;
    @MockBean ApplicationEvent applicationEvent;
    @MockBean AdminBlackMemberService adminService;
    @MockBean MemberRepository memberRepository;
    @MockBean PostServiceImpl postService;
    @MockBean JwtProvider jwtProvider;
    @MockBean CustomOauth2Handler customOauth2Handler;
    @MockBean CustomOauth2Service customOauth2Service;


    @Test
    @DisplayName("사용자 수 전제 조회")
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
    @DisplayName("사용자 조회 API 무한 페이징 테스트")
    @CustomMockAdmin
    void getMemberAllInfo() throws Exception {

        String loginId = "testId";
        List<MemberDto> memberDtoList = List.of(MemberDto.builder().username("회원1").email("email1").loginId(loginId).nickname("닉네임1").join_date(LocalDate.now()).build(),
                MemberDto.builder().username("회원2").email("email2").loginId("loginId2").nickname("닉네임2").join_date(LocalDate.now()).build());

        boolean hasNext = false;
        MemberInfoResponse memberInfoResponse = new MemberInfoResponse(memberDtoList, hasNext);
        given(adminService.memberInfos(isNull(),any())).willReturn(memberInfoResponse);

        mockMvc.perform(get("/api/admin/members/info"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberInfoes.[0].username").value("회원1"))
                .andExpect(jsonPath("$.data.memberInfoes.[0].loginId").value(loginId))
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
                        .param("memberIds", list.stream().map(String::valueOf).collect(Collectors.joining(","))))
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

        mockMvc.perform(get("/api/admin/members/search?loginId="+loginId+"&nickname="+nickname))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.memberInfoes.[0].username").value("회원1"))
                .andExpect(jsonPath("$.data.memberInfoes.[0].loginId").value(loginId))
                .andExpect(jsonPath("$.data.size()").value(2));
    }


    @Test
    @DisplayName("블랙리스트 이메일 임시 차단테스트")
    @CustomMockAdmin
    void unBlock() throws Exception {

        when(adminService.temporarilyUnblockUser(anyLong())).thenReturn(false);

        mockMvc.perform(post("/api/admin/blacklist/temporary-unblock/1"))
                .andDo(print())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("임시 차단 유뮤"));
    }

}