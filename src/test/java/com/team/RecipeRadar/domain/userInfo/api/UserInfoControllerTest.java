package com.team.RecipeRadar.domain.userInfo.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.userInfo.dto.info.*;
import com.team.RecipeRadar.domain.userInfo.application.UserInfoService;
import com.team.RecipeRadar.domain.userInfo.utils.CookieUtils;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import com.team.RecipeRadar.global.security.oauth2.KakaoUserDisConnectServiceImpl;
import com.team.RecipeRadar.global.security.oauth2.NaverUserDisConnectServiceImpl;
import com.team.RecipeRadar.global.security.oauth2.provider.Oauth2UrlProvider;
import com.team.mock.CustomMockUser;
import lombok.extern.slf4j.Slf4j;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserInfoController.class)
@Slf4j
class UserInfoControllerTest {

    @MockBean private UserInfoService userInfoService;
    @MockBean KakaoUserDisConnectServiceImpl kakaoUserDisConnectService;
    @MockBean NaverUserDisConnectServiceImpl naverUserDisConnectService;
    @Autowired private MockMvc mockMvc;

    @MockBean CookieUtils cookieUtils;
    @MockBean Oauth2UrlProvider oauth2UrlProvider;
    @MockBean JwtProvider jwtProvider;
    @MockBean MemberRepository memberRepository;
    @MockBean CustomOauth2Handler customOauth2Handler;
    @MockBean CustomOauth2Service customOauth2Service;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("사용자 정보 조회 API 성공 테스트")
    @CustomMockUser
    public void userInfo_Success() throws Exception {
        // Given
        String loginId = "test";
        Cookie cookie = new Cookie("login-id", "fakeCookie");
        UserInfoResponse expectedResponse = UserInfoResponse.builder()
                .nickName("나만냉장고")
                .username("홍길동")
                .loginType("normal")
                .email("test@naver.com").build();

        given(userInfoService.validUserToken(anyString(), anyString())).willReturn(true);

        given(userInfoService.getMembers(eq(loginId), anyString())).willReturn(expectedResponse);

        // When, Then
        mockMvc.perform(get("/api/user/info/{login-id}", loginId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("조회성공"))
                .andExpect(jsonPath("$.data.username").value("홍길동"))
                .andExpect(jsonPath("$.data.nickName").value("나만냉장고"))
                .andExpect(jsonPath("$.data.email").value("test@naver.com"))
                .andExpect(jsonPath("$.data.loginType").value("normal"));
    }

    @Test
    @DisplayName("사용자 정보 조회 API 실패 테스트")
    @CustomMockUser
    public void userInfo_AccessDeniedException() throws Exception {
        // Given
        String loginId = "testId";

        Cookie cookie = new Cookie("login-id", "fakeCookie");
        given(userInfoService.validUserToken(anyString(), anyString())).willReturn(true);
        when(userInfoService.getMembers(eq(loginId), anyString()))
                .thenThrow(new AccessDeniedException("Access Denied"));

        // When, Then
        mockMvc.perform(get("/api/user/info/{login-id}", loginId)
                        .cookie(cookie))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }


    @Test
    @DisplayName("사용자 닉네임 업데이트 API 성공 테스트")
    @CustomMockUser
    public void userInfoUpdate_SuccessTest() throws Exception {
        String nickName ="newNickname";
        String loginId = "testId";

        UserInfoUpdateNickNameRequest request = new UserInfoUpdateNickNameRequest();
        request.setNickName(nickName);
        request.setLoginId(loginId);
        Cookie cookie = new Cookie("login-id", "fakeCookie");
        given(userInfoService.validUserToken(anyString(), anyString())).willReturn(true);

        mockMvc.perform(put("/api/user/info/update/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("변경 성공"));

        verify(userInfoService).updateNickName(eq(nickName), eq(loginId), anyString());
        verify(userInfoService, times(1)).updateNickName(eq(nickName), eq(loginId), anyString());
    }

    @Test
    @DisplayName("사용자 닉네임 업데이트 API 실패 테스트")
    @CustomMockUser
    public void userInfoUpdate_FailTest() throws Exception {
        String nickName ="newNickname";
        String loginId = "testId";

        UserInfoUpdateNickNameRequest request = new UserInfoUpdateNickNameRequest();
        Cookie cookie = new Cookie("login-id", "fakeCookie");
        request.setNickName(nickName);
        request.setLoginId(loginId);

        given(userInfoService.validUserToken(anyString(), anyString())).willReturn(true);
        doThrow(new AccessDeniedException("접근 불가한 페이지")).when(userInfoService).updateNickName(eq(nickName), eq(loginId), anyString());

        mockMvc.perform(put("/api/user/info/update/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("접근 불가한 페이지"));
    }

    @Test
    @DisplayName("사용자 페이지 이메일 변경 성공 테스트")
    @CustomMockUser
    void userInfo_Update_Email_Success_Test() throws Exception {
        // Given
        String email = "test@email.com";
        String loginId = "loginId";
        String code = "123456";
        String loginType = "normal";
        UserInfoEmailRequest request = new UserInfoEmailRequest(email, code, loginId,loginType);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        given(userInfoService.validUserToken(anyString(), anyString())).willReturn(true);
        // When, Then
        mockMvc.perform(put("/api/user/info/update/email")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("변경 성공"));

        verify(userInfoService).updateEmail(eq(email),eq(code),eq(loginId),anyString(),anyString());
        verify(userInfoService,times(1)).updateEmail(eq(email),eq(code),eq(loginId),anyString(),anyString());
    }


    @Test
    @DisplayName("사용자 페이지 이메일 변경 실패 테스트")
    @CustomMockUser
    void userInfo_Update_Email_Fail_Test() throws Exception {
        // Given
        String email = "test@email.com";
        String loginId = "loginId";
        String code = "123456";
        String loginType = "normal";
        UserInfoEmailRequest request = new UserInfoEmailRequest(email, code, loginId,loginType);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        given(userInfoService.validUserToken(anyString(), anyString())).willReturn(true);
        doThrow(new BadRequestException("접근할수 없는 페이지 입니다.")).when(userInfoService).updateEmail(eq(email),eq(code),eq(loginId),anyString(),anyString());

        // When, Then
        mockMvc.perform(put("/api/user/info/update/email")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("접근할수 없는 페이지 입니다."));

        verify(userInfoService).updateEmail(eq(email),eq(code),eq(loginId),anyString(),anyString());
        verify(userInfoService,times(1)).updateEmail(eq(email),eq(code),eq(loginId),anyString(),anyString());
    }

    @Test
    @CustomMockUser
    @DisplayName("회원 탈퇴 성공 테스트")
    void Delete_Member_Site_SUCCESS() throws Exception {
        String loginId = "loginId";
        String username = "test";

        UserDeleteIdRequest userDeleteIdRequest = new UserDeleteIdRequest(loginId, true);
        Cookie cookie = new Cookie("login-id", "fakeCookie");
        given(userInfoService.validUserToken(anyString(), anyString())).willReturn(true);

        doNothing().when(userInfoService).deleteMember(loginId, true, username); // username 값 설정

        mockMvc.perform(delete("/api/user/info/disconnect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .content(objectMapper.writeValueAsString(userDeleteIdRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("탈퇴 성공"));

        //void 반환타입 1회 실행되었는지 확인
        verify(userInfoService, times(1)).deleteMember(anyString(), anyBoolean(), eq(username));
    }


    @Test
    @CustomMockUser
    @DisplayName("약관 미체크시 예외")
    void none_MissCheck_throw()throws Exception{
        String loginId = "test";

        UserDeleteIdRequest userDeleteIdRequest = new UserDeleteIdRequest(loginId, false);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        given(userInfoService.validUserToken(anyString(), anyString())).willReturn(true);
        doThrow(new BadRequestException("약관 미체크")).when(userInfoService).deleteMember(loginId,false,"test");

        mockMvc.perform(delete("/api/user/info/disconnect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .content(objectMapper.writeValueAsString(userDeleteIdRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("약관 미체크"))
                .andDo(print());
    }

    @Test
    @CustomMockUser
    @DisplayName("잘못된 사용자 접근시 예외")
    void no_accessMember_throw()throws Exception{
        String loginId = "loginId";

        UserDeleteIdRequest userDeleteIdRequest = new UserDeleteIdRequest(loginId, false);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        given(userInfoService.validUserToken(anyString(), anyString())).willReturn(true);
        doThrow(new AccessDeniedException("잘못된 접근 이거나 일반 사용자만 가능합니다.")).when(userInfoService).deleteMember(loginId,false,"test");

        mockMvc.perform(delete("/api/user/info/disconnect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .content(objectMapper.writeValueAsString(userDeleteIdRequest)))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("잘못된 접근 이거나 일반 사용자만 가능합니다."))
                .andDo(print());
    }
    
    @Test
    @CustomMockUser
    @DisplayName("사용자가 즐겨찾기한 레시피 제목 페이징")
    void bookmark_page() throws Exception {
        Long memberId =  1l;
        List<RecipeDto> list = List.of(RecipeDto.builder().id(1l).title("레시피1").build(),RecipeDto.builder().id(2l).title("레시피2").build(),RecipeDto.builder().id(3l).title("레시피3").build());

        UserInfoBookmarkResponse userInfoBookmarkResponse = new UserInfoBookmarkResponse(false, list);
        given(userInfoService.userInfoBookmark(eq(memberId),isNull(),any(Pageable.class))).willReturn(userInfoBookmarkResponse);

        Cookie cookie = new Cookie("login-id", "cookie");
        mockMvc.perform(get("/api/user/info/bookmark")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.hasNext").value(false))
                .andExpect(jsonPath("$.data.bookmark_list.size()").value(3));
    }

    @Test
    @CustomMockUser
    @DisplayName("사용자가 즐겨찾기한 레시피 제목 페이징(쿠키가 없을때 접근)")
    void bookmark_page_NONECOOKIE() throws Exception {
        Long memberId =  1l;
        List<RecipeDto> list = List.of(RecipeDto.builder().id(1l).title("레시피1").build(),RecipeDto.builder().id(2l).title("레시피2").build(),RecipeDto.builder().id(3l).title("레시피3").build());

        UserInfoBookmarkResponse userInfoBookmarkResponse = new UserInfoBookmarkResponse(false, list);
        given(userInfoService.userInfoBookmark(eq(memberId),isNull(),any(Pageable.class))).willReturn(userInfoBookmarkResponse);

        mockMvc.perform(get("/api/user/info/bookmark"))
                .andDo(print())
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("쿠키값이 없을때 접근"));
    }

    @Test
    @CustomMockUser(id = 2l)
    @DisplayName("사용자가 즐겨찾기한 레시피 제목 페이징(사용자가아닌 다른 사용자가 접근시)")
    void aasdasd() throws Exception {
        Long memberId =  1l;
        List<RecipeDto> list = List.of(RecipeDto.builder().id(1l).title("레시피1").build(),RecipeDto.builder().id(2l).title("레시피2").build(),RecipeDto.builder().id(3l).title("레시피3").build());

        UserInfoBookmarkResponse userInfoBookmarkResponse = new UserInfoBookmarkResponse(false, list);
        given(userInfoService.userInfoBookmark(eq(memberId),isNull(),any(Pageable.class))).willReturn(userInfoBookmarkResponse);
        Cookie cookie = new Cookie("login-id", "cookie");

        mockMvc.perform(get("/api/user/info/bookmark")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data").doesNotExist());
    }
}