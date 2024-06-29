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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

    @Mock MemberRepository memberRepository;
    @Mock EmailVerificationRepository emailVerificationRepository;
    @Mock RefreshTokenRepository refreshTokenRepository;
    @Mock AccountRetrievalRepository accountRetrievalRepository;
    @Mock NoticeRepository noticeRepository;
    @Mock NotificationRepository notificationRepository;
    @Mock SinUpService sinUpService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock QuestionRepository questionRepository;
    @Mock AccountRetrievalEmailServiceImpl emailService;

    @InjectMocks MemberServiceImpl memberService;

    private static Long userId = 1l;
    private static Long adminId = 1l;

    private List<Member> members;

    @BeforeEach
    void setUp(){
        members = List.of(
                Member.builder().id(userId).loginId("testId").username("이름").nickName("닉네임").email("이메일").roles("ROLE_USER").login_type("normal").build(),
                Member.builder().id(adminId).loginId("adminId").username("어드민").nickName("어드민닉네임").email("어드민이메일").roles("ROLE_ADMIN").login_type("normal").build());
    }
    
    @Test
    @DisplayName("비밀번호 일치 테스트")
    void duplicatePassword(){
        Map<String, Boolean> password1 = memberService.duplicatePassword("1234565", "1234");
        Map<String, Boolean> password2 = memberService.duplicatePassword("1234565", "1234565");

        assertThat(password1.get("duplicate_password")).isFalse();
        assertThat(password2.get("duplicate_password")).isTrue();
    }
    
    @Test
    @DisplayName("비밀번호가 강력한 비밀번호인지 테스트")
    void checkPasswordStrength(){
        Map<String, Boolean> failPWD = memberService.checkPasswordStrength("123456");
        Map<String, Boolean> successPWD = memberService.checkPasswordStrength("asdASD12!@");
        
        assertThat(failPWD.get("passwordStrength")).isFalse();
        assertThat(successPWD.get("passwordStrength")).isTrue();
    }

    @Test
    @DisplayName("사용자 페이지의 개인 정보를 불러오는 테스트")
    void getMembers() {
        when(memberRepository.findById(userId)).thenReturn(Optional.of(members.get(0)));

        // getMembers 메서드 호출 및 반환된 UserInfoResponse 객체 저장
        UserInfoResponse members = memberService.getMembers(userId);

        // 검증
        assertThat(members.getUsername()).isEqualTo("이름");
        assertThat(members.getNickName()).isEqualTo("닉네임");
        assertThat(members.getEmail()).isEqualTo("이메일");
        assertThat(members.getLoginType()).isEqualTo("normal");
    }

    @Test
    @DisplayName("사용자 페이지에서 닉네임을 변경하는 테스트")
    void update_NickName_success(){
        when(memberRepository.findById(eq(userId))).thenReturn(Optional.of(members.get(0)));

        memberService.updateNickName("변경된 닉네임",userId);

        assertThat(members.get(0).getNickName()).isEqualTo("변경된 닉네임");
        assertThat(members.get(0).getNickName()).isNotEqualTo("닉네임");
    }

    @Test
    @DisplayName("사용자 페이지에서 이메일 변경 성공 테스트")
    void update_email_success(){
        String AfterEmail = "afterTest@eamil.com";
        EmailVerification emailVerification = EmailVerification.builder().expiredAt(LocalDateTime.now().plusMinutes(3)).code(123456).email(AfterEmail).build();

        when(memberRepository.findById(userId)).thenReturn(Optional.of(members.get(0)));
        when(emailVerificationRepository.findByEmailAndCode(anyString(),anyInt())).thenReturn(emailVerification);

        Map<String,Boolean> emailValidMap = new HashMap<>();
        emailValidMap.put("duplicateEmail",true);
        emailValidMap.put("useEmail",true);
        emailValidMap.put("blackListEmail",true);

        when(sinUpService.emailValid(AfterEmail)).thenReturn(emailValidMap);

        Map<String,Boolean> verifyCodeMap = new HashMap<>();
        verifyCodeMap.put("isVerifyCode",true);

        memberService.updateEmail(AfterEmail,123456, userId);

        assertThat(members.get(0).getEmail()).isEqualTo(AfterEmail);
        verify(emailService,times(3)).deleteCode(anyString(),anyInt());
    }

    @Test
    @DisplayName("사용자 페이지에서 이메일 변경주 이메일 관련 예외 테스트")
    void update_email_fail() {
        String afterEmail = "afEmail@email.com";

        Map<String, Boolean> emailValidMap = new HashMap<>();
        emailValidMap.put("duplicateEmail", false);
        emailValidMap.put("useEmail", true);
        emailValidMap.put("blackListEmail",true);

        Map<String, Boolean> verifyCodeMap = new HashMap<>();
        verifyCodeMap.put("isVerifyCode", true);

        when(memberRepository.findById(eq(userId))).thenReturn(Optional.of(members.get(0)));

        when(sinUpService.emailValid(eq(afterEmail))).thenReturn(emailValidMap);
        when(emailVerificationRepository.findByEmailAndCode(anyString(), anyInt())).thenReturn(null);

        assertThatThrownBy(() -> memberService.updateEmail(afterEmail, 123456, userId))
                .isInstanceOf(InvalidIdException.class);
    }


    @Test
    @DisplayName("사용자의 로그인아이드를 통해 탈퇴 테스트")
    void deleteByLoginId(){
        when(memberRepository.findByLoginId(anyString())).thenReturn(members.get(0));

        memberService.deleteByLoginId("testId");

        verify(noticeRepository, times(1)).deleteMemberId(anyLong());
        verify(notificationRepository, times(1)).deleteMember(anyLong());
        verify(refreshTokenRepository, times(1)).DeleteByMemberId(anyLong());
        verify(questionRepository, times(1)).deleteAllByMemberId(anyLong());
        verify(memberRepository, times(1)).delete(any(Member.class));
    }

    @Test
    @DisplayName("회원 탈퇴 테스트")
    void delete_Member(){
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(members.get(0)));
        memberService.deleteMember(members.get(0).getId(),true);

        verify(memberRepository, times(1)).delete(any());
    }

    @Test
    @DisplayName("회원 탈퇴시 예외 발생 테스트")
    void delete_Member_throws(){
        when(memberRepository.findById(anyLong())).thenThrow(NoSuchDataException.class);

        assertThatThrownBy(() -> memberService.deleteMember(userId,true)).isInstanceOf(NoSuchDataException.class);

    }
}
