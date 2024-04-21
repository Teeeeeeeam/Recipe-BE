package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.AccountRetrieval;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.AccountRetrieval.UpdatePasswordRequest;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.email.application.MailService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.payload.ControllerApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * 아이디 찾기시에 사용되는 로직
     * @param username  가입한 사용자 이름
     * @param email     가입했던 이메일
     * @param code      이메일로 전송된 인증번호
     * @return      List로 반환
     */
    public List<Map<String ,String>> findLoginId(String username, String email, int code) {
        List<MemberDto> byUsernameAndEmail = memberRepository.findByUsernameAndEmail(username, email).stream().map(MemberDto::from).collect(Collectors.toList());

    List<Map<String,String>> list = new LinkedList<>();     //순서를 보장하기 위해 LinkedList 사용

    Boolean emailCode = emailCode(email,code);        //인증번호

    Map<String, String> errorMap = new LinkedHashMap<>();

    if (emailCode) {            //인증번호 검증
        if (byUsernameAndEmail.isEmpty()) {
            errorMap.put("가입 정보", "해당 정보로 가입된 회원은 없습니다.");
            list.add(errorMap);
        } else{
            for (MemberDto memberDto : byUsernameAndEmail) {
                Map<String, String> loginInfo = new LinkedHashMap<>();
                loginInfo.put("로그인 타입", memberDto.getLogin_type());
                loginInfo.put("로그인 정보", memberDto.getLoginId());
                list.add(loginInfo);
            }
            mailService.deleteCode(email,code);
        }
    } else {
        errorMap.put("인증 번호", "인증번호가 일치하지 않습니다.");
        list.add(errorMap);
    }

    return list;
}

    /**
     * 비밀번호 찾기 로직
     * @param username  가입한 사용자이름
     * @param loginId   가입한 로그인 아이디
     * @param email     가입한 이메일
     * @return
     */
    @Override
    public Map<String, Object> findPwd(String username, String loginId, String email,int code) {
        Boolean memberExists = memberRepository.existsByUsernameAndLoginIdAndEmail(username, loginId, email);

        Boolean emailedCode = emailCode(email,code);

        Map<String, Object> map = new LinkedHashMap<>();

        if (memberExists&&emailedCode){
            AccountRetrieval save = accountRetrievalRepository.save(AccountRetrieval.builder().loginId(loginId).build());
            String verificationId = save.getVerificationId();
            String token = new String(Base64.getEncoder().encode(verificationId.getBytes()));
            map.put("token",token);
            mailService.deleteCode(email,code);
        }

        map.put("회원 정보", memberExists);
        map.put("이메일 인증", emailedCode);

        return map;
    }

    /**
     * 비밀번호 수정 API DB-> 10분마다 스케쥴 이벤트 발생
     * @param updatePasswordRequest MemberDto 객체(username, loginId, password, passwordRe)
     * @param id        인증 ID ((UUID 생성된)Base64 인코딩된 문자열)
     * @return ControllerApiResponse 객체
     */
    public ControllerApiResponse updatePassword(UpdatePasswordRequest updatePasswordRequest, String id){

        String validId = new String(Base64.getDecoder().decode(id.getBytes()));

        Boolean aBoolean = accountRetrievalRepository.existsByVerificationId(validId);              // 해당 ID가 있는지 체크

        Member byLoginId = memberRepository.findByLoginId(updatePasswordRequest.getLoginId());
        if (byLoginId==null) throw new NoSuchElementException("가입된 아이디를 찾을수 없습니다.");

        Map<String, Boolean> stringBooleanMap = memberService.checkPasswordStrength(updatePasswordRequest.getPassword());     //성공시 true , 실패시 false
        Map<String, Boolean> stringBooleanMap1 = memberService.duplicatePassword(updatePasswordRequest.getPassword(), updatePasswordRequest.getPasswordRe());        //성공시 true , 실패시 false

        ControllerApiResponse apiResponse = null;

        if (aBoolean) {
            if (stringBooleanMap.get("passwordStrength")) {
                if (stringBooleanMap1.get("duplicate_password")) {
                    byLoginId.update(passwordEncoder.encode(updatePasswordRequest.getPassword()));
                    apiResponse = new ControllerApiResponse(true, "비밀번호 변경 성공");
                    accountRetrievalRepository.deleteByVerificationId(validId);
                } else
                   throw new BadRequestException("비밀번호가 일치하지 않습니다.");
            } else
                throw new BadRequestException("비밀번호가 안전하지 않습니다.");
        }else
            throw new BadRequestException("잘못된 접근");

        return apiResponse;
    }

    /**
     * 이메일 인증시 인증번호가 유효한지 체크
     * @param code  사용자가 입력한 인증번호
     * @return  일치시 -> true 불일치 false
     */
    public Boolean emailCode(String email, int code){
        Map<String, Boolean> stringBooleanMap = mailService.verifyCode(email, code);
        if (stringBooleanMap.get("isVerifyCode")){
            return true;
        }
        return false;
    }

}