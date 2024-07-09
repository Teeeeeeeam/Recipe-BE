package com.team.RecipeRadar.domain.account.application;

import com.team.RecipeRadar.domain.account.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.member.application.user.MemberService;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.account.domain.AccountRetrieval;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.account.dto.request.UpdatePasswordRequest;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.email.application.MailService;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class AccountRetrievalServiceImpl implements AccountRetrievalService{

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    @Qualifier("AccountEmail")
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AccountRetrievalRepository accountRetrievalRepository;


    /**
     * 아이디 찾기시에 이메일과 전송된 이메일통해 사용자의 이름과 이메일로 가입된 사용자의 아이디 있는지 찾는 메서드
     * Map의 로그인 타입과 사용자의 로그인 아이디를 반환
     */
    public List<Map<String ,String>> findLoginId(String username, String email, int code) {
        List<MemberDto> memberDtos = memberRepository.findByUsernameAndEmail(username, email).stream().map(MemberDto::from).collect(Collectors.toList());

        if (!emailCodeValid(email, code)) {
            return List.of(Map.of("인증 번호", "인증번호가 일치하지 않습니다."));
        }
        if (memberDtos.isEmpty()) {
            return List.of(Map.of("가입 정보", "해당 정보로 가입된 회원은 없습니다."));
        }

        mailService.deleteCode(email, code);
        return memberDtos.stream()
                .map(dto -> Map.of("login_type", dto.getLogin_type(), "login_info", dto.getLoginId()))
                .collect(Collectors.toList());
    }

    /**
     * 비밀번호 찾기 검증 메서드
     * 이메일과 인증번호를 통해 검증하고 파라미터러 넘어온 값들이 일치하는 정보가 있다면 3분짜리 비밀번호 변경에사용할수 있는 토큰을 번환
     */
    @Override
    public String findPwd(String username, String loginId, String email,int code) {
        Boolean memberExists = memberRepository.existsByUsernameAndLoginIdAndEmail(username, loginId, email);
        if (!memberExists) throw new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER);
        Boolean emailCodeValid = emailCodeValid(email,code);

        if (!emailCodeValid)
            throw new InvalidIdException("인증번호가 일치하지 않거나 유효시간이 지났습니다.");
        String token  = accountRetrievalRepository.save(AccountRetrieval.createAccount(loginId,3)).getVerificationId();
        mailService.deleteCode(email,code);

        return token;
    }

    /**
     * 비밀번호를 새로 변경하는 메서드
     * 비밀번호 찾기 검증시에 발급된 토큰 값을 통해 비밀번호를 새롭게 변경 (DB -> 10분마다 현재 시간기준으로 토큰을 삭제)
     */
    public void updatePassword(UpdatePasswordRequest updatePasswordRequest, String token){

        String validId = new String(Base64.getDecoder().decode(token.getBytes()));

        AccountRetrieval accountRetrieval = accountRetrievalRepository.findById(validId).orElseThrow(() -> new IllegalStateException("접근할수 없습니다."));

        expiredAt(accountRetrieval);

        Member member = memberRepository.findByLoginId(updatePasswordRequest.getLoginId());
        if (member==null) throw new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER);

        // 비밀번호 유효성 검사 진행
        validatePassword(updatePasswordRequest);

        //비밀번호 저장
        member.update(passwordEncoder.encode(updatePasswordRequest.getPassword()));
        
        //비밀번호 성공시 인증 DB 에서 삭제
        accountRetrievalRepository.deleteByVerificationId(validId);
    }

    /**
     * 만료되었는지 검증 하느 메서드
     */
    private static void expiredAt(AccountRetrieval accountRetrieval) {
        if(accountRetrieval.getExpireAt().isBefore(LocalDateTime.now())){
            throw new IllegalStateException("잘못된 접근");
        }
    }

    /**
     * 비밀번호 유효성 검사를 하는 메서드
     * 강력도와 중복성의 대해서 검사를 하며 실패시 예외 발생
     */
    private void validatePassword(UpdatePasswordRequest updatePasswordRequest) {
        if (!memberService.checkPasswordStrength(updatePasswordRequest.getPassword()).getOrDefault("passwordStrength", false)) {
            throw new IllegalStateException("비밀번호가 안전하지 않습니다.");
        }
        if (!memberService.duplicatePassword(updatePasswordRequest.getPassword(), updatePasswordRequest.getPasswordRe()).getOrDefault("duplicate_password", false)) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }
    }

    /**
     * 이메일 인증시 인증번호가 유효한지 체크
     * @param code  사용자가 입력한 인증번호
     * @return  일치시 -> true 불일치 false
     */
    public Boolean emailCodeValid(String email, int code){
        Boolean isVerified = mailService.verifyCode(email, code).get("isVerified");
        Boolean isExpired = mailService.verifyCode(email, code).get("isExpired");
        return isVerified && isExpired;
    }

}