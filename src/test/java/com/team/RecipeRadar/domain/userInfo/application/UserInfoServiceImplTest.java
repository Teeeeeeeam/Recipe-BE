package com.team.RecipeRadar.domain.userInfo.application;

import com.team.RecipeRadar.domain.member.application.MemberServiceImpl;
import com.team.RecipeRadar.domain.member.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.AccountRetrieval;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoResponse;
import com.team.RecipeRadar.global.email.application.AccountRetrievalEmailServiceImpl;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UserInfoServiceImplTest {


    @Mock
    MemberRepository memberRepository;

    @Mock
    MemberServiceImpl memberService;
    @Mock
    AccountRetrievalEmailServiceImpl accountRetrievalEmailService;
    @Mock
    AccountRetrievalRepository accountRetrievalRepository;

    @Mock
    JWTRefreshTokenRepository jwtRefreshTokenRepository;
    @Mock
    PasswordEncoder passwordEncoder;

    @InjectMocks
    UserInfoServiceImpl userInfoService;

    @Test
    @DisplayName("사용자 페이지의 개인 정보를 불러오는 테스트")
    void getMembers() {
        // 가짜 토큰과 가짜 로그인 ID 설정
        String loginId = "testId";
        String username = "이름";

        // 가짜 멤버 객체 생성
        Member member = Member.builder()
                .id(1L)
                .loginId(loginId)
                .username(username)
                .nickName("닉네임")
                .email("이메일")
                .login_type("normal").build();

        // 가짜 멤버 객체 반환 설정
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        // getMembers 메서드 호출 및 반환된 UserInfoResponse 객체 저장
        UserInfoResponse members = userInfoService.getMembers(loginId, username);

        // 검증
        assertThat(members.getUsername()).isEqualTo("이름");
        assertThat(members.getNickName()).isEqualTo("닉네임");
        assertThat(members.getEmail()).isEqualTo("이메일");
        assertThat(members.getLoginType()).isEqualTo("normal");
    }

    @Test
    @DisplayName("사용자 페이지의 다른 사용자 접근시 예외 발생 테스트")
    void getMembers_AccessDenied() {
        String loginId = "testId";
        String differentUsername= "differentUsername";       //다른 사용자 ID


        // 가짜 멤버 객체 생성
        Member member = Member.builder()
                .id(1L)
                .loginId(loginId)
                .username("이름")
                .nickName("닉네임")
                .email("이메일")
                .login_type("normal").build();

        // 가짜 멤버 객체 반환 설정
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        // AccessDeniedException 예외 발생
        assertThatThrownBy(() -> userInfoService.getMembers(loginId,differentUsername)).isInstanceOf(AccessDeniedException.class);

    }

    @Test
    @DisplayName("사용자 페이지에서 닉네임을 변경하는 테스트")
    void update_NickName_success(){
        String loginId = "loginId";
        String aFNickName = "afterName";
        String authName = "username";


        Member member = Member.builder().id(1l).loginId(loginId).username("username").nickName("before").login_type("normal").build();

        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        userInfoService.updateNickName(aFNickName,loginId,authName);

        assertThat(member.getNickName()).isEqualTo(aFNickName);
    }

    @Test
    @DisplayName("사용자 페이지에서 닉네임을 변경중 실패하는 테스트")
    void update_NickName_fail(){
        String loginId = "loginId";
        String aFNickName = "afterName";
        String fakeAuthName = "fakeUsername";


        Member member = Member.builder().id(1l).loginId(loginId).username("username").nickName("before").build();

        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        assertThatThrownBy(() -> userInfoService.updateNickName(aFNickName,loginId,fakeAuthName)).isInstanceOf(AccessDeniedException.class);
    }

    @Test
    @DisplayName("사용자 페이지에서 이메일 변경 성공 테스트")
    void update_email_success(){
        String loginId = "testId";
        String AfterEmail = "afEmail@email.com";
        String autName = "username";
        Member member = Member.builder().username("username").nickName("nickName").loginId(loginId).email("test@email.com").login_type("normal").build();

        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        Map<String,Boolean> emailValidMap = new HashMap<>();
        emailValidMap.put("duplicateEmail",true);
        emailValidMap.put("useEmail",true);

        when(memberService.emailValid(AfterEmail)).thenReturn(emailValidMap);

        Map<String,Boolean> verifyCodeMap = new HashMap<>();
        verifyCodeMap.put("isVerifyCode",true);

        when(memberService.verifyCode(AfterEmail,123456)).thenReturn(verifyCodeMap);

        userInfoService.updateEmail(AfterEmail,"123456",loginId,autName,"normal");

        assertThat(member.getEmail()).isEqualTo(AfterEmail);
    }

    @Test
    @DisplayName("사용자 페이지에서 이메일 변경주 이메일 관련 예외 테스트")
    void update_email_fail(){
        String loginId = "testId";
        String AfterEmail = "afEmail@email.com";
        String autName = "username";
        Member member = Member.builder().username("username").nickName("nickName").loginId(loginId).email("test@email.com").login_type("normal").build();

        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        Map<String,Boolean> emailValidMap = new HashMap<>();
        emailValidMap.put("duplicateEmail",false);
        emailValidMap.put("useEmail",true);

        when(memberService.emailValid(AfterEmail)).thenReturn(emailValidMap);

        Map<String,Boolean> verifyCodeMap = new HashMap<>();
        verifyCodeMap.put("isVerifyCode",true);

        when(memberService.verifyCode(AfterEmail,123456)).thenReturn(verifyCodeMap);

        assertThatThrownBy(() -> userInfoService.updateEmail(AfterEmail,"123456",loginId,autName,"normal")).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("사용자 페이지 접근시 비밀번호가 일치하면 토큰 생성")
    void cookie_Token_Access_PasswordMatch() {
        String loginId = "testId";
        String autName = "username";
        Member member = Member.builder()
                .username("username")
                .nickName("nickName")
                .password("1234")
                .loginId(loginId)
                .email("test@email.com")
                .login_type("normal")
                .build();

        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        boolean machPassword = true; // 비밀번호 일치하는 상황
        when(passwordEncoder.matches("1234", member.getPassword())).thenReturn(machPassword);

        String uuidId = UUID.randomUUID().toString();
        LocalDateTime plusMinutes = LocalDateTime.now().plusMinutes(20);
        AccountRetrieval accountRetrieval = AccountRetrieval.builder()
                .verificationId(uuidId)
                .loginId(loginId)
                .expireAt(plusMinutes)
                .build();
        when(accountRetrievalRepository.save(any())).thenReturn(accountRetrieval);

        String token = userInfoService.userToken(loginId, autName, "1234","normal");

        assertThat(token).isEqualTo(uuidId);
    }


    @Test
    @DisplayName("사용자 페이지 접근시 비밀번호가 일치하지않아 예외 발생")
    void cookie_Token_Access_PasswordMatch_Fail() {
        String loginId = "testId";
        String autName = "username";
        Member member = Member.builder()
                .username("username")
                .nickName("nickName")
                .password("1234")
                .loginId(loginId)
                .email("test@email.com")
                .login_type("normal")
                .build();

        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        boolean machPassword = false; // 비밀번호 일치하는 상황
        when(passwordEncoder.matches("1111", member.getPassword())).thenReturn(machPassword);

        assertThatThrownBy(() -> userInfoService.userToken(loginId,autName,"1111","normal")).isInstanceOf(BadRequestException.class);
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void delete_Member(){
        Member member = Member.builder()
                .id(1l)
                .login_type("normal")
                .username("test")
                .loginId("loginId").build();

        when(memberRepository.findByLoginId("loginId")).thenReturn(member);

        userInfoService.deleteMember("loginId",true,"test");

        verify(memberRepository, times(1)).deleteById(member.getId());
    }

    @Test
    @DisplayName("회원 탈퇴시 예외 발생 테스트")
    void delete_Member_throws(){

       when(memberRepository.findByLoginId("loginid")).thenThrow(AccessDeniedException.class);

       assertThatThrownBy(() -> userInfoService.deleteMember("loginid",true,"test")).isInstanceOf(AccessDeniedException.class);

    }
}