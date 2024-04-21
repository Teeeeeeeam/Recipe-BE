package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.AccountRetrieval;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.UpdatePasswordRequest;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.email.application.AccountRetrievalEmailServiceImpl;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class AccountRetrievalServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock AccountRetrievalEmailServiceImpl emailService;
    @Mock MemberService memberService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock AccountRetrievalRepository accountRetrievalRepository;

    @InjectMocks AccountRetrievalServiceImpl accountRetrievalService;


    @Test
    @DisplayName("아이디 찾기시에 일반,소셜 로그인 존재시")
    void findLoginId() {
        String email = "test@email.com";
        String username = "username";

        Member normal = Member.builder().id(1L).login_type("normal").loginId("testId").email(email).username(username).build();     //일반사용자
        Member social = Member.builder().id(2L).login_type("kakao").loginId("testId").email(email).username(username).build();      // 소셜 로그인 사용자

        List<Member> list = new ArrayList<>();
        list.add(normal);
        list.add(social);

        when(memberRepository.findByUsernameAndEmail(eq(username), eq(email))).thenReturn(list);        //리스트로 반환

        Integer realCode = 1234;
        when(emailService.verifyCode(email, realCode)).thenReturn(Collections.singletonMap("isVerifyCode", true));


        List<Map<String, String>> loginId = accountRetrievalService.findLoginId(username, email, realCode);     //반환

        assertThat(loginId.get(0).get("로그인 타입")).isEqualTo("normal");
        assertThat(loginId.get(1).get("로그인 타입")).isEqualTo("kakao");
        assertThat(loginId.size()).isEqualTo(2);
    }


    @Test
    @DisplayName("아이디 찾기시에 인증번호 틀리는 테스트")
    void emailCode_fail() {
        String email = "test@email.com";
        String username = "username";

        Member normal = Member.builder().id(1L).login_type("normal").loginId("testId").email(email).username(username).build();     //일반사용자
        Member social = Member.builder().id(2L).login_type("kakao").loginId("testId").email(email).username(username).build();      // 소셜 로그인 사용자

        List<Member> list = new ArrayList<>();
        list.add(normal);
        list.add(social);

        when(memberRepository.findByUsernameAndEmail(eq(username), eq(email))).thenReturn(list);        //리스트로 반환

        int fakeCode = 12;
        int realCode = 1234;

        // 잘못된 코드로 verifyCode를 호출하는 대신, 올바른 코드를 사용하여 호출해야 합니다.
        when(emailService.verifyCode(email, fakeCode)).thenReturn(Collections.singletonMap("isVerifyCode", false));
        // 이메일인증코드

        List<Map<String, String>> loginId = accountRetrievalService.findLoginId(username, email, fakeCode);     //반환

        assertThat(loginId.get(0).get("인증 번호")).isEqualTo("인증번호가 일치하지 않습니다.");
        assertThat(loginId.size()).isEqualTo(1);
    }


    @Test
    @DisplayName("사용자 정보가 없을때")
    void find_fail_read_member_info() {
        String email = "test@email.com";
        String username = "username";

        Member normal = Member.builder().id(1L).login_type("normal").loginId("testId").email(email).username(username).build();     //일반사용자
        Member social = Member.builder().id(2L).login_type("kakao").loginId("testId").email(email).username(username).build();      // 소셜 로그인 사용자

        List<Member> list = new ArrayList<>();
        list.add(normal);
        list.add(social);

        when(memberRepository.findByUsernameAndEmail(eq("등록되지 않은 사용자"), eq(email))).thenReturn(Collections.emptyList());

        int realCode = 1234;
        when(emailService.verifyCode(email, realCode)).thenReturn(Collections.singletonMap("isVerifyCode", true));

        List<Map<String, String>> loginId = accountRetrievalService.findLoginId("등록되지 않은 사용자", email, realCode);     //반환
        assertThat(loginId.get(0).get("가입 정보")).isEqualTo("해당 정보로 가입된 회원은 없습니다.");
        assertThat(loginId.size()).isEqualTo(1);
    }

    @Test
    @DisplayName("비밀번호 찾기 성공시")
    void find_pwd_success(){
        String email = "test@email.com";
        String username = "username";
        String loginId = "loginId";
        int code= 1234; // 코드는 int 형태로 설정되어 있는 것으로 보입니다.

        // 가짜 회원이 존재함을 설정
        when(memberRepository.existsByUsernameAndLoginIdAndEmail(username, loginId, email)).thenReturn(true);

        // 이메일 서비스가 인증 코드를 확인하여 true를 반환하도록 설정
        when(emailService.verifyCode(email, code)).thenReturn(Collections.singletonMap("isVerifyCode", true));

        AccountRetrieval accountRetrieval = AccountRetrieval.builder().verificationId("accountRetrieval").loginId(loginId).build();

        // accountRetrievalRepository.save 메서드에 대한 스텁 설정
        when(accountRetrievalRepository.save(any(AccountRetrieval.class))).thenReturn(accountRetrieval);

        Map<String, Object> pwd = accountRetrievalService.findPwd(username, loginId, email, code);

        // 회원 정보와 이메일 인증이 모두 true인지 확인
        assertThat(pwd.get("회원 정보")).isEqualTo(true);
        assertThat(pwd.get("이메일 인증")).isEqualTo(true);
        assertThat(pwd.get("token")).isNotNull();
    }


    @Test
    @DisplayName("비밀번호 찾기 실패시")
    void find_pwd_false(){
        String email = "test@email.com";
        String username = "username";
        String loginId = "loginId";
        int code = 1234; // 코드는 int 형식으로 설정되어 있는 것으로 보입니다.

        // 아이디와 사용자 이름과 이메일로 등록된 회원이 없음을 설정
        when(memberRepository.existsByUsernameAndLoginIdAndEmail(username, loginId, email)).thenReturn(false);

        // 임의의 코드 반환
        int realCode = 1234;
        when(emailService.verifyCode(email, realCode)).thenReturn(Collections.singletonMap("isVerifyCode", false));

        Map<String, Object> pwd = accountRetrievalService.findPwd(username, loginId, email, code);

        assertThat(pwd.get("회원 정보")).isEqualTo(false);
        assertThat(pwd.get("이메일 인증")).isEqualTo(false);
    }


    @Test
    @DisplayName("비밀번호 변경 성공시")
    void update_password_success(){
        String verificationId = UUID.randomUUID().toString();
        String password = "asd123QWE!@";
        String passwordRe = "asd123QWE!@";
        String loginId = "loginId";

        String token = Base64.getEncoder().encodeToString(verificationId.getBytes());

        UpdatePasswordRequest updatePasswordDto = new UpdatePasswordRequest(loginId, password, passwordRe);
        when(accountRetrievalRepository.existsByVerificationId(verificationId)).thenReturn(true);

        Member member = Member.builder().id(1l).loginId(loginId).username("username").password("asd").build();
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        Map<String, Boolean> passwordStrengthMap = new HashMap<>();
        passwordStrengthMap.put("passwordStrength", true);
        Map<String, Boolean> duplicatePasswordMap = new HashMap<>();
        duplicatePasswordMap.put("duplicate_password", true);
        when(memberService.duplicatePassword(anyString(),anyString())).thenReturn(duplicatePasswordMap);
        when(memberService.checkPasswordStrength(any())).thenReturn(passwordStrengthMap);

        ControllerApiResponse apiResponse = accountRetrievalService.updatePassword(updatePasswordDto, token);

        assertThat(apiResponse.isSuccess()).isTrue();
        assertThat(apiResponse.getMessage()).isEqualTo("비밀번호 변경 성공");
    }

    @Test
    @DisplayName("비밀번호 실패 - 안전한 비밀번호 아닐시")
    void update_password_fail(){
        String verificationId = UUID.randomUUID().toString();
        String password = "asd123QWE!@";
        String passwordRe = "asd123QWE!@";
        String loginId = "loginId";

        String token = Base64.getEncoder().encodeToString(verificationId.getBytes());
        UpdatePasswordRequest updatePasswordDto = new UpdatePasswordRequest(loginId, password, passwordRe);

        when(accountRetrievalRepository.existsByVerificationId(verificationId)).thenReturn(true);

        Member member = Member.builder().id(1l).username("username").password("asd").build();
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        Map<String, Boolean> passwordStrengthMap = new HashMap<>();
        passwordStrengthMap.put("passwordStrength", false);
        Map<String, Boolean> duplicatePasswordMap = new HashMap<>();
        duplicatePasswordMap.put("duplicate_password", true);
        when(memberService.duplicatePassword(anyString(),anyString())).thenReturn(duplicatePasswordMap);
        when(memberService.checkPasswordStrength(any())).thenReturn(passwordStrengthMap);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            accountRetrievalService.updatePassword(updatePasswordDto, token);
        });

        // 예외 메시지가 일치하는지 확인
        assertThat(exception.getMessage()).isEqualTo("비밀번호가 안전하지 않습니다.");
    }
    @Test
    @DisplayName("비밀번호 실패 - 비밀번호가 일치하지 않을시")
    void update_password_fail2(){
        String verificationId = UUID.randomUUID().toString();
        String password = "asd123QWE!@";
        String passwordRe = "asd123QWE!";
        String loginId = "loginId";

        String token = Base64.getEncoder().encodeToString(verificationId.getBytes());
        UpdatePasswordRequest updatePasswordDto = new UpdatePasswordRequest(loginId, password, passwordRe);

        when(accountRetrievalRepository.existsByVerificationId(verificationId)).thenReturn(true);

        Member member = Member.builder().id(1l).loginId(loginId).username("username").password("asd").build();
        when(memberRepository.findByLoginId(loginId)).thenReturn(member);

        Map<String, Boolean> passwordStrengthMap = new HashMap<>();
        passwordStrengthMap.put("passwordStrength", true);
        Map<String, Boolean> duplicatePasswordMap = new HashMap<>();
        duplicatePasswordMap.put("duplicate_password", false);
        when(memberService.duplicatePassword(anyString(),anyString())).thenReturn(duplicatePasswordMap);
        when(memberService.checkPasswordStrength(any())).thenReturn(passwordStrengthMap);

        BadRequestException exception = assertThrows(BadRequestException.class, () -> {
            accountRetrievalService.updatePassword(updatePasswordDto, token);
        });

        // 예외 메시지가 일치하는지 확인
        assertThat(exception.getMessage()).isEqualTo("비밀번호가 일치하지 않습니다.");
    }
}