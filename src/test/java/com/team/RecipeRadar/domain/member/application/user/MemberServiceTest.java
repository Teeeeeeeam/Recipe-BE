package com.team.RecipeRadar.domain.member.application.user;

import com.team.RecipeRadar.domain.account.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.email.application.AccountRetrievalEmailServiceImpl;
import com.team.RecipeRadar.domain.email.dao.EmailVerificationRepository;
import com.team.RecipeRadar.domain.email.domain.EmailVerification;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.response.UserInfoResponse;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notification.dao.NotificationRepository;
import com.team.RecipeRadar.domain.qna.dao.question.QuestionRepository;
import com.team.RecipeRadar.global.auth.dao.RefreshTokenRepository;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock MemberRepository memberRepository;
    @Mock EmailVerificationRepository emailVerificationRepository;
    @Mock AccountRetrievalRepository accountRetrievalRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock NoticeRepository noticeRepository;
    @Mock NotificationRepository notificationRepository;
    @Mock SinUpService sinUpService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock QuestionRepository questionRepository;
    @Mock AccountRetrievalEmailServiceImpl emailService;

    @InjectMocks MemberServiceImpl memberService;

    private static Long memberId = 1l;

    @Test
    @DisplayName("사용자 페이지의 개인 정보를 불러오는 테스트")
    void getMembers() {
        // 가짜 토큰과 가짜 로그인 ID 설정
        String loginId = "testId";
        String username = "이름";

        // 가짜 멤버 객체 생성
        Member member = Member.builder()
                .id(memberId)
                .loginId(loginId)
                .username(username)
                .nickName("닉네임")
                .email("이메일")
                .login_type("normal").build();

        // 가짜 멤버 객체 반환 설정
        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));

        // getMembers 메서드 호출 및 반환된 UserInfoResponse 객체 저장
        UserInfoResponse members = memberService.getMembers(memberId);

        // 검증
        assertThat(members.getUsername()).isEqualTo("이름");
        assertThat(members.getNickName()).isEqualTo("닉네임");
        assertThat(members.getEmail()).isEqualTo("이메일");
        assertThat(members.getLoginType()).isEqualTo("normal");
    }

    @Test
    @DisplayName("사용자 페이지에서 닉네임을 변경하는 테스트")
    void update_NickName_success(){
        String loginId = "loginId";
        String aFNickName = "afterName";

        Member member = Member.builder().id(memberId).loginId(loginId).username("username").nickName("before").login_type("normal").build();

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        memberService.updateNickName(aFNickName,1L);

        assertThat(member.getNickName()).isEqualTo(aFNickName);
    }

    @Test
    @DisplayName("사용자 페이지에서 이메일 변경 성공 테스트")
    void update_email_success(){
        String loginId = "testId";
        String AfterEmail = "afEmail@email.com";
        Member member = Member.builder().id(memberId).username("username").nickName("nickName").loginId(loginId).email("test@email.com").login_type("normal").build();
        EmailVerification emailVerification = EmailVerification.builder().expiredAt(LocalDateTime.now().plusMinutes(3)).code(123456).email(AfterEmail).build();

        when(memberRepository.findById(memberId)).thenReturn(Optional.of(member));
        when(emailVerificationRepository.findByEmailAndCode(anyString(),anyInt())).thenReturn(emailVerification);

        Map<String,Boolean> emailValidMap = new HashMap<>();
        emailValidMap.put("duplicateEmail",true);
        emailValidMap.put("useEmail",true);

        when(sinUpService.emailValid(AfterEmail)).thenReturn(emailValidMap);

        Map<String,Boolean> verifyCodeMap = new HashMap<>();
        verifyCodeMap.put("isVerifyCode",true);

        memberService.updateEmail(AfterEmail,123456,memberId);

        assertThat(member.getEmail()).isEqualTo(AfterEmail);
    }

    @Test
    @DisplayName("사용자 페이지에서 이메일 변경주 이메일 관련 예외 테스트")
    void update_email_fail() {
        String loginId = "testId";
        String afterEmail = "afEmail@email.com";
        Member member = Member.builder().id(memberId).username("username").nickName("nickName").loginId(loginId).email("test@email.com").login_type("normal").build();

        Map<String, Boolean> emailValidMap = new HashMap<>();
        emailValidMap.put("duplicateEmail", false);
        emailValidMap.put("useEmail", true);

        Map<String, Boolean> verifyCodeMap = new HashMap<>();
        verifyCodeMap.put("isVerifyCode", true);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        when(sinUpService.emailValid(eq(afterEmail))).thenReturn(emailValidMap);
        when(emailVerificationRepository.findByEmailAndCode(anyString(), anyInt())).thenReturn(null);

        assertThatThrownBy(() -> memberService.updateEmail(afterEmail, 123456, memberId))
                .isInstanceOf(InvalidIdException.class);
    }



    @Test
    @DisplayName("회원 탈퇴 테스트")
    void delete_Member(){
        Member member = Member.builder()
                .id(memberId)
                .login_type("normal")
                .username("test")
                .loginId("loginId").build();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));
        memberService.deleteMember(member.getId(),true);

        verify(memberRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("회원 탈퇴시 예외 발생 테스트")
    void delete_Member_throws(){

        when(memberRepository.findById(anyLong())).thenThrow(NoSuchDataException.class);

        assertThatThrownBy(() -> memberService.deleteMember(memberId,true)).isInstanceOf(NoSuchDataException.class);

    }
}
