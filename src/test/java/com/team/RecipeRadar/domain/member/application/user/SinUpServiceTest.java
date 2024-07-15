package com.team.RecipeRadar.domain.member.application.user;

import com.team.RecipeRadar.domain.blackList.dao.BlackListRepository;
import com.team.RecipeRadar.domain.email.application.JoinEmailServiceImpl;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import java.util.*;

import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SinUpServiceTest {

    @Mock MemberRepository memberRepository;
    @Mock PasswordEncoder passwordEncoder;
    @Mock BlackListRepository blackListRepository;
    @Mock JoinEmailServiceImpl emailService;

    @InjectMocks SinUpServiceImpl sinUpService;


    private Member member;
    private MemberDto memberDto;

    @BeforeEach
    void setUp(){
        member = Member.builder().id(1l).username("사용자 실명").nickName("사용자 닉네임").loginId("loginId").email("emailuse@test.com").password("asdASD12!@").build();
        memberDto = MemberDto.from(member);
    }


    @Test
    @DisplayName("사용자 회원가입 테스트")
    void joinMember() {
        when(passwordEncoder.encode(any(String.class))).thenReturn("encodedPassword");
        when(memberRepository.save(any(Member.class))).thenReturn(member);

        memberDto.setPassword("plainPassword");
        sinUpService.joinMember(memberDto);

        verify(memberRepository, times(1)).save(any(Member.class));
        assertThat("encodedPassword").isEqualTo(memberDto.getPassword());
    }

    @Test
    @DisplayName("회원가입 유효성 검사 테스트")
    void validationOfSignUp() {
        when(memberRepository.existsByLoginId(anyString())).thenReturn(false);
        when(memberRepository.findByEmail(anyString())).thenReturn(List.of(member));
        when(blackListRepository.existsByEmail(anyString())).thenReturn(false);
        when(emailService.verifyCode(anyString(), anyInt())).thenReturn(Map.of("isVerified", true, "isExpired", true));

        memberDto.setCode(123456);
        memberDto.setPasswordRe("asdASD12!@");
        boolean validationOfSignUp = sinUpService.ValidationOfSignUp(memberDto);

        assertThat(validationOfSignUp).isTrue();
        verify(emailService, times(1)).deleteCode(anyString(), anyInt());
    }

    @Test
    @DisplayName("아이디 중복 검사 테스트")
    void loginIdValid() {
        when(memberRepository.existsByLoginId(anyString())).thenReturn(false);
        when(memberRepository.findByCaseSensitiveLoginId(anyString())).thenReturn(null);

        Map<String, Boolean> result = sinUpService.LoginIdValid("uniqueLoginId");
        assertThat(result.get("useLoginId")).isTrue();
    }

    @Test
    @DisplayName("중복된 아이디 예외 테스트")
    void loginIdInvalid() {
        when(memberRepository.existsByLoginId(anyString())).thenReturn(true);

        assertThatThrownBy(() ->  sinUpService.LoginIdValid("duplicateLoginId"))
                .isInstanceOf(InvalidIdException.class);
    }

    @Test
    @DisplayName("이메일 유효성 검사 테스트")
    void emailValid() {
        when(blackListRepository.existsByEmail(anyString())).thenReturn(false);
        when(memberRepository.findByEmail(anyString())).thenReturn(List.of(member));

        Map<String, Boolean> result = sinUpService.emailValid("test@example.com");

        assertThat(result.get("duplicateEmail")).isTrue();
        assertThat(result.get("useEmail")).isTrue();
        assertThat(result.get("blackListEmail")).isTrue();
    }

    @Test
    @DisplayName("닉네임 유효성 검사 테스트")
    void nickNameValid() {
        when(memberRepository.existsByNickName(anyString())).thenReturn(false);
        sinUpService.nickNameValid("사용가능");
    }

    @Test
    @DisplayName("닉네임 예외 테스트")
    void nickNameInvalid() {
        assertThatThrownBy(() -> sinUpService.nickNameValid("불가능")).isInstanceOf(InvalidIdException.class);
    }

    @Test
    @DisplayName("회원가입 오류 메시지 테스트")
    void validationErrorMessage() {
        Map<String, Boolean> validationResult = new HashMap<>();
        validationResult.put("duplicate_password", false);
        validationResult.put("isLoginValid", false);
        validationResult.put("duplicateEmail", false);
        validationResult.put("code", false);

        when(emailService.verifyCode(anyString(), anyInt())).thenReturn(Map.of("code", false));
        when(memberRepository.existsByLoginId(anyString())).thenReturn(true);
        when(memberRepository.findByEmail(anyString())).thenReturn(List.of(member));

        memberDto.setCode(123456);
        Map<String, String> errorMessages = sinUpService.ValidationErrorMessage(memberDto);
        assertThat("비밀번호가 일치하지 않습니다.").isEqualTo(errorMessages.get("passwordRe"));
        assertThat("중복된 아이디입니다.").isEqualTo(errorMessages.get("loginId"));
        assertThat("인증 번호가 일치하지 않습니다.").isEqualTo(errorMessages.get("code"));
    }
}