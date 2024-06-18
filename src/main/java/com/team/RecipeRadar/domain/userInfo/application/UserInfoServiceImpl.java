package com.team.RecipeRadar.domain.userInfo.application;

import com.team.RecipeRadar.domain.email.dao.EmailVerificationRepository;
import com.team.RecipeRadar.domain.email.domain.EmailVerification;
import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.AccountRetrieval;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.dao.bookmark.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoBookmarkResponse;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoResponse;
import com.team.RecipeRadar.domain.email.application.MailService;
import com.team.RecipeRadar.global.exception.ex.*;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final MemberRepository memberRepository;
    private final JWTRefreshTokenRepository jwtRefreshTokenRepository;
    private final AccountRetrievalRepository accountRetrievalRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailVerificationRepository emailVerificationRepository;
    private final RecipeBookmarkRepository recipeBookmarkRepository;

    @Qualifier("AccountEmail")
    private final MailService mailService;
    private final MemberService memberService;


    /**
     * 사용자의 정보(이름,이메일,닉네임,로그인 타입)등을 조회하는 로직
     * @param memberId   로그인아이디
     * @return  userInfoResponse 데이터 반환
     */
    @Override
    public UserInfoResponse getMembers(Long memberId) {
        Member member = getMember(memberId);

        return UserInfoResponse.builder()
                .nickName(member.getNickName())
                .username(member.getUsername())
                .email(member.getEmail())
                .loginType(member.getLogin_type()).build();
    }

    /**
     * 사용자 닉네임 변경 메서드
     */
    public void updateNickName(String nickName,Long memberId){
        Member member = getMember(memberId);
        member.updateNickName(nickName);
    }

    /**
     *  사용자의 이메일 변경
     * @param email     변경할 이메일
     * @param code      이메일 인증번호
     */
    @Override
    public void updateEmail(String email, Integer code,Long memberId) {
        Member member = getMember(memberId);
        validateNormalUser(member);

        Map<String, Boolean> emailValid = memberService.emailValid(email);//이메일 유효성검사
        EmailVerification emailVerification = emailVerificationRepository.findByEmailAndCode(email, code);

        for (Map.Entry<String, Boolean> valid :emailValid.entrySet()) {
            if (valid.getValue() && emailVerification != null && emailVerification.expired(emailVerification)) {
                member.updateEmail(email);
                mailService.deleteCode(email,code);
            }else throw new InvalidIdException("인증번호 및 이메일이 잘못되었습니다.");
        }
    }

    /**
     * 일반 사용자 페이지 접근시 비밀번호 재 검증후 쿠키생성
     * @param loginId       로그인 아이디
     * @param authenticationName    시큐리티 정보
     * @param password      비밀번호
     * @return      ID반환
     */
    @Override
    public String userToken(String loginId,String authenticationName, String password,String loginType) {

        Member member = memberRepository.findByLoginId(loginId);
        if (member == null || !member.getUsername().equals(authenticationName)) {
            throw new AccessDeniedException("잘못된 접근입니다.");
        }

        if (loginType.equals("normal")) {
            boolean matches = passwordEncoder.matches(password, member.getPassword());
            if (!matches) {
                throw new BadRequestException("비밀번호가 일치하지 않습니다.");
            }
        }

        LocalDateTime expireTime = LocalDateTime.now().plusMinutes(20); //쿠키의 만료 시간을 20분 후로 설정
        AccountRetrieval accountRetrieval = AccountRetrieval.builder().loginId(member.getLoginId()).expireAt(expireTime).build();
        return accountRetrievalRepository.save(accountRetrieval).getVerificationId();

    }

    /**
     * 회원 탈퇴 로직
     * @param loginId 사용자 아이디
     * @param checkType 약관 동의
     * @param authenticationName 현재 로그인한 사용자 이름
     */
    @Override
    public void deleteMember(String loginId, boolean checkType, String authenticationName) {
        Member member = memberRepository.findByLoginId(loginId);;

        if (member == null || !member.getUsername().equals(authenticationName)||!member.getLogin_type().equals("normal"))
            throw new AccessDeniedException("잘못된 접근 이거나 일반 사용자만 가능합니다.");

        if (!checkType)
            throw new BadRequestException("약관 동의를 주세요");

        jwtRefreshTokenRepository.DeleteByMemberId(member.getId());
        memberRepository.deleteById(member.getId());
    }

    @Override
    public boolean validUserToken(String encodeToken,String loginId) {
        if(encodeToken==null){
            throw new ForbiddenException("잘못된 접근입니다.");
        }
        String decodeToken = new String(Base64.getDecoder().decode(encodeToken.getBytes()));
        AccountRetrieval accountRetrieval = accountRetrievalRepository.findById(decodeToken).orElseThrow(() -> new AccessDeniedException("잘못된 접근입니다."));
        Member byLoginId = memberRepository.findByLoginId(loginId);
        if(!accountRetrieval.getExpireAt().isAfter(LocalDateTime.now())){       // 토큰 시간이 현재 시간보다 전이라면 false
            return false;
        }
        if (byLoginId==null||!accountRetrieval.getLoginId().equals(byLoginId.getLoginId())){
            return false;
        }
        return true;
    }

    @Override
    public UserInfoBookmarkResponse userInfoBookmark(Long memberId, Long lastId, Pageable pageable) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("사용자를 찾을수 없습니다."));

        Slice<RecipeDto> recipeDtos = recipeBookmarkRepository.userInfoBookmarks(member.getId(), lastId, pageable);

        return new UserInfoBookmarkResponse(recipeDtos.hasNext(),recipeDtos.getContent());
    }

    private Member throwsMember(String loginId, String authName) {
        Member member = memberRepository.findByLoginId(loginId);
        if (member == null || !member.getUsername().equals(authName)) {
            throw new AccessDeniedException("잘못된 접근입니다.");
        }
        return member;
    }

    private Member getMember(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));
        return member;
    }

    private static void validateNormalUser(Member member) {
        if (!member.getLogin_type().equals("normal"))
            throw new AccessDeniedException("일반 사용자만 가능합니다.");
    }
}
