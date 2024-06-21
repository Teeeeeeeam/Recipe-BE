package com.team.RecipeRadar.domain.member.api.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.application.user.MemberService;
import com.team.RecipeRadar.domain.member.dto.response.UserInfoResponse;
import com.team.RecipeRadar.domain.member.dto.rqeust.UserDeleteIdRequest;
import com.team.RecipeRadar.domain.member.dto.rqeust.UserInfoEmailRequest;
import com.team.RecipeRadar.domain.member.dto.rqeust.UserInfoUpdateNickNameRequest;
import com.team.RecipeRadar.global.conig.TestConfig;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import com.team.RecipeRadar.global.utils.CookieUtils;
import com.team.mock.CustomMockUser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;

import javax.servlet.http.Cookie;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(TestConfig.class)
@WebMvcTest(MemberController.class)
public class MemberControllerTest {

    @Autowired private MockMvc mockMvc;
    @MockBean ApplicationEvent applicationEvent;
    @MockBean CookieUtils cookieUtils;
    @MockBean MemberService memberService;
    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("사용자 정보 조회 API 성공 테스트")
    @CustomMockUser
    public void userInfo_Success() throws Exception {
        // Given
        Cookie cookie = new Cookie("login-id", "fakeCookie");
        UserInfoResponse expectedResponse = UserInfoResponse.builder()
                .nickName("나만냉장고")
                .username("홍길동")
                .loginType("normal")
                .email("test@naver.com").build();

        given(memberService.getMembers(anyLong())).willReturn(expectedResponse);

        // When, Then
        mockMvc.perform(get("/api/user/info")
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
        Cookie cookie = new Cookie("login-id", "fakeCookie");
        when(memberService.getMembers(anyLong()))
                .thenThrow(new IllegalArgumentException("Access Denied"));

        // When, Then
        mockMvc.perform(get("/api/user/info/")
                        .cookie(cookie))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }


    @Test
    @DisplayName("사용자 닉네임 업데이트 API 성공 테스트")
    @CustomMockUser
    public void userInfoUpdate_SuccessTest() throws Exception {
        String nickName ="newNickname";

        UserInfoUpdateNickNameRequest request = new UserInfoUpdateNickNameRequest();
        request.setNickName(nickName);

        Cookie cookie = new Cookie("login-id", "fakeCookie");

        mockMvc.perform(put("/api/user/info/update/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("변경 성공"));

        verify(memberService).updateNickName(eq(nickName),anyLong());
        verify(memberService, times(1)).updateNickName(eq(nickName),anyLong());
    }

    @Test
    @DisplayName("사용자 닉네임 업데이트 API 실패 테스트")
    @CustomMockUser
    public void userInfoUpdate_FailTest() throws Exception {
        String nickName ="newNickname";

        UserInfoUpdateNickNameRequest request = new UserInfoUpdateNickNameRequest();
        Cookie cookie = new Cookie("login-id", "fakeCookie");
        request.setNickName(nickName);

        doThrow(new IllegalArgumentException("접근 불가한 페이지")).when(memberService).updateNickName(eq(nickName), anyLong());

        mockMvc.perform(put("/api/user/info/update/nickname")
                        .content(objectMapper.writeValueAsString(request))
                        .cookie(cookie)
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("접근 불가한 페이지"));
    }

    @Test
    @DisplayName("사용자 페이지 이메일 변경 성공 테스트")
    @CustomMockUser
    void userInfo_Update_Email_Success_Test() throws Exception {
        String email = "test@email.com";
        int code = 123456;
        UserInfoEmailRequest request = new UserInfoEmailRequest(email, code);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        mockMvc.perform(put("/api/user/info/update/email")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("변경 성공"));

        verify(memberService).updateEmail(eq(email),eq(code),anyLong());
        verify(memberService,times(1)).updateEmail(eq(email),eq(code),anyLong());
    }


    @Test
    @DisplayName("사용자 페이지 이메일 변경 실패 테스트")
    @CustomMockUser
    void userInfo_Update_Email_Fail_Test() throws Exception {
        String email = "test@email.com";
        int code = 123456;
        UserInfoEmailRequest request = new UserInfoEmailRequest(email, code);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        doThrow(new InvalidIdException("접근할수 없는 페이지 입니다.")).when(memberService).updateEmail(eq(email),eq(code),anyLong());

        mockMvc.perform(put("/api/user/info/update/email")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON).cookie(cookie))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("접근할수 없는 페이지 입니다."));

        verify(memberService).updateEmail(eq(email),eq(code),anyLong());
        verify(memberService,times(1)).updateEmail(eq(email),eq(code),anyLong());
    }

    @Test
    @CustomMockUser
    @DisplayName("회원 탈퇴 성공 테스트")
    void Delete_Member_Site_SUCCESS() throws Exception {

        UserDeleteIdRequest userDeleteIdRequest = new UserDeleteIdRequest(true);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        doNothing().when(memberService).deleteMember(anyLong(),eq(true)); // username 값 설정

        mockMvc.perform(delete("/api/user/info/disconnect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .content(objectMapper.writeValueAsString(userDeleteIdRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("탈퇴 성공"));

        //void 반환타입 1회 실행되었는지 확인
        verify(memberService, times(1)).deleteMember(anyLong(), anyBoolean());
    }


    @Test
    @CustomMockUser
    @DisplayName("약관 미체크시 예외")
    void none_MissCheck_throw()throws Exception{
        UserDeleteIdRequest userDeleteIdRequest = new UserDeleteIdRequest(false);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        doThrow(new InvalidIdException("약관 미체크")).when(memberService).deleteMember(anyLong(),eq(false));

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
        UserDeleteIdRequest userDeleteIdRequest = new UserDeleteIdRequest(false);
        Cookie cookie = new Cookie("login-id", "fakeCookie");

        doThrow(new UnauthorizedException("잘못된 접근 이거나 일반 사용자만 가능합니다.")).when(cookieUtils).validCookie(anyString(),anyString());

        mockMvc.perform(delete("/api/user/info/disconnect")
                        .contentType(MediaType.APPLICATION_JSON)
                        .cookie(cookie)
                        .content(objectMapper.writeValueAsString(userDeleteIdRequest)))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("잘못된 접근 이거나 일반 사용자만 가능합니다."))
                .andDo(print());
    }

}
