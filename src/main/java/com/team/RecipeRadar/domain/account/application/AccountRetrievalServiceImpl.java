package com.team.RecipeRadar.domain.account.application;

import com.team.RecipeRadar.domain.account.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.member.application.user.MemberService;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.account.domain.AccountRetrieval;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.account.dto.request.UpdatePasswordRequest;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.email.application.MailService;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AccountRetrievalServiceImpl implements AccountRetrievalService{

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    @Qualifier("AccountEmail")
    private final MailService mailService;
    private final PasswordEncoder passwordEncoder;
    private final AccountRetrievalRepository accountRetrievalRepository;


    /**
     * 아이디 찾기시에 사용되는 메서드
     * @param username  가입한 사용자 이름
     * @param email     가입했던 이메일
     * @param code      이메일로 전송된 인증번호
     * @return      List로 반환
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
     * 비밀번호 찾는 메서드
     * @param username  가입한 사용자이름
     * @param loginId   가입한 로그인 아이디
     * @param email     가입한 이메일
     * @return
     */
    @Override
    public String findPwd(String username, String loginId, String email,int code) {
        Boolean memberExists = memberRepository.existsByUsernameAndLoginIdAndEmail(username, loginId, email);
        if (!memberExists) throw new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER);
        Boolean emailCodeValid = emailCodeValid(email,code);

        String token = "";

        if (memberExists&&emailCodeValid){
             token  = accountRetrievalRepository.save(AccountRetrieval.createAccount(loginId,3)).getVerificationId();
            mailService.deleteCode(email,code);
        }

        return token;
    }

    /**
     * 비밀번호 수정 API DB-> 10분마다 스케쥴 이벤트 발생
     * @param updatePasswordRequest MemberDto 객체(username, loginId, password, passwordRe)
     * @param token        인증 ID ((UUID 생성된)Base64 인코딩된 문자열)
     * @return ControllerApiResponse 객체
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
     * @param updatePasswordRequest
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
        return mailService.verifyCode(email, code).getOrDefault("isVerifyCode",false);
    }

}