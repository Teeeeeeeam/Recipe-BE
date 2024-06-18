package com.team.RecipeRadar.domain.userInfo.application;

import com.team.RecipeRadar.domain.email.dao.EmailVerificationRepository;
import com.team.RecipeRadar.domain.email.domain.EmailVerification;
import com.team.RecipeRadar.domain.member.application.MemberServiceImpl;
import com.team.RecipeRadar.domain.member.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.AccountRetrieval;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.bookmark.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoBookmarkResponse;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoResponse;
import com.team.RecipeRadar.domain.email.application.AccountRetrievalEmailServiceImpl;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.SliceImpl;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class UserInfoServiceImplTest {


    @Mock MemberRepository memberRepository;
    @Mock RecipeBookmarkRepository recipeBookmarkRepository;
    @Mock MemberServiceImpl memberService;
    @Mock EmailVerificationRepository emailVerificationRepository;
    @Mock AccountRetrievalRepository accountRetrievalRepository;
    @Mock JWTRefreshTokenRepository jwtRefreshTokenRepository;
    @Mock AccountRetrievalEmailServiceImpl accountRetrievalEmailService;


    @Mock PasswordEncoder passwordEncoder;

    @InjectMocks UserInfoServiceImpl userInfoService;

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
        UserInfoResponse members = userInfoService.getMembers(memberId);

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

        userInfoService.updateNickName(aFNickName,1L);

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

        when(memberService.emailValid(AfterEmail)).thenReturn(emailValidMap);

        Map<String,Boolean> verifyCodeMap = new HashMap<>();
        verifyCodeMap.put("isVerifyCode",true);

        userInfoService.updateEmail(AfterEmail,123456,memberId);

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

        when(memberService.emailValid(eq(afterEmail))).thenReturn(emailValidMap);
        when(emailVerificationRepository.findByEmailAndCode(anyString(), anyInt())).thenReturn(null);

        assertThatThrownBy(() -> userInfoService.updateEmail(afterEmail, 123456, memberId))
                .isInstanceOf(InvalidIdException.class);
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
    
    @Test
    @DisplayName("사용자가 즐겨찾기한 레시피 제목 조회 테스트")
    void bookmark_page(){

        Long memberId = 1l;
        Member member = Member.builder().id(memberId).nickName("닉네임").build();

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));

        Pageable pageRequest = PageRequest.of(0, 10);

        List<RecipeDto> list = List.of(RecipeDto.builder().id(1l).title("레시피1").build(),RecipeDto.builder().id(2l).title("레시피2").build(),RecipeDto.builder().id(3l).title("레시피3").build());
        boolean hasNext =false;

        SliceImpl<RecipeDto> recipeDtoSlice = new SliceImpl<>(list, pageRequest, hasNext);

        when(recipeBookmarkRepository.userInfoBookmarks(eq(memberId),isNull(),eq(pageRequest))).thenReturn(recipeDtoSlice);

        UserInfoBookmarkResponse userInfoBookmarkResponse = userInfoService.userInfoBookmark(memberId, null, pageRequest);
        assertThat(userInfoBookmarkResponse.getBookmark_list()).hasSize(3);
        assertThat(userInfoBookmarkResponse.getHasNext()).isFalse();
    }

}