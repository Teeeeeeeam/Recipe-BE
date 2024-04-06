package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.member.dto.valid.PasswordStrengthDto;
import com.team.RecipeRadar.global.email.application.MailService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ServerErrorException;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private static String LOGIN_TYPE = "normal";

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Qualifier("JoinEmail")
    private final MailService mailService;


    @Override
    public Member saveEntity(Member member) {
        return memberRepository.save(member);
    }

    @Override
    public Member saveDto(MemberDto memberDto) {
        Member memberEntity = Member.builder()
                .loginId(memberDto.getLoginId())
                .password(passwordEncoder.encode(memberDto.getPassword()))
                .username(memberDto.getUsername())
                .nickName(memberDto.getNickName())
                .login_type(LOGIN_TYPE)
                .email(memberDto.getEmail())
                .join_date(LocalDate.now())
                .roles("ROLE_USER")
                .verified(true)
                .build();
        return memberRepository.save(memberEntity);
    }

    @Override
    public Member findByLoginId(String loginId) {
        Member byLoginId = memberRepository.findByLoginId(loginId);
        if (byLoginId == null) {
           log.info("오류발생");
        }
        return byLoginId;
    }

    /**
     * 회원가입시 아이디 중복검사 및 아이디 조건체크(대소문자 구문)
     * @param loginId 회원가입시 정보
     * @return  아이디가 이미 사용 중이거나 조건이 불충분하면 false를 반환하고, 모두 만족시 true를 반환합니다
     */
    public Map<String, Boolean> LoginIdValid(String loginId) {
        Map<String, Boolean> result = new LinkedHashMap<>();
            boolean isLoginIdValid = isLoginIdValid(loginId);
            Member member = memberRepository.findByCaseSensitiveLoginId(loginId);
            if (member==null && isLoginIdValid) {
                result.put("use_loginId", true);
            }else
                throw new BadRequestException("사용할수 없는 아이디입니다.");
            return result;
    }

    /**
     * 아이디 조건검사 대문자나 소문자(5~16)자리 입력시 사용할수있다.
     * @param loginId
     * @return 대문자나 소문자일시 true, 아닐시 false
     */
    private boolean isLoginIdValid(String loginId) {
        Member member = memberRepository.findByCaseSensitiveLoginId(loginId);

        boolean  pattern= Pattern.compile("^[a-zA-Z0-9]{5,16}$").matcher(loginId).find();
        if (member!=null||!pattern) {
            return false;
        }

        return true;
    }

    /**
     * 주어진 회원 정보를 통해 비밀번호 중복여부를 체크하고, 결과를 반환합니다.
     * @param password 첫번쨰 비밀번호
     * @param passwordRe 다시 입력한 비밀번호
     * @return 비밀번호 일치여부를 Map<String,Boolean>타입으로 반환
     */
    public Map<String, Boolean> duplicatePassword(String password,String passwordRe) {
        Map<String, Boolean> result = new LinkedHashMap<>();

        result.put("duplicate_password", password.equals(passwordRe));
        return result;
    }

    /**
     * 회원가입시 닉네임이 유효한지 확인합니다.
     * @param nickName 회원가입시 사용할 닉네임
     * @return 닉네임이 유효할 경우 true, 그렇지 않을 경우 false
     */
    @Override
    public Map<String, Boolean> nickNameValid(String nickName) {
        try{
            Map<String, Boolean> result = new LinkedHashMap<>();

            boolean valid = Pattern.compile("^[a-zA-Z0-9가-힣]{4,}$").matcher(nickName).matches();
            result.put("nickNameValid",valid);
            return result;
        }catch (Exception e){
            throw new RuntimeException();
        }
    }

    /**
     * 회원가입시 모든 조건검사를 하지 않았을때 회원가입을 하지 못함.
     * @param memberDto 회원가입의 정보
     * @return  모두 사용시 true, 하나라도 검사 안했을시 false
     */
    public boolean ValidationOfSignUp(MemberDto memberDto,int code) {
        Map<String, Boolean> validationResult = validateSignUp(memberDto,code);

        for (Map.Entry<String, Boolean> entry : validationResult.entrySet   ()) {
            if (!entry.getValue()) {
                log.info("entry={}",entry.getKey());
                log.error("Failed validation: {}", entry.getKey());
                return false;
            }
        }
        mailService.deleteCode(memberDto.getEmail(),code);

        return true;
    }

    /**
     * 강력한 비밀번호인지 확인 "특수문자","0~9","소문자","대문자","8자리이상" 포함 여부 확인
     * @param password 회원가입시 사용자정보
     * @return 강력한 비밀번호시 true, 아닐시 false 반환
     */
    public Map<String, Boolean> checkPasswordStrength(String password) {
        try {
            Map<String, Boolean> result = new LinkedHashMap<>();
            // 특수 문자 포함 여부, 대문자 알파벳 포함 여부, 비밀번호의 길이가 8자리 이상인지를 한 번에 확인
            boolean checkStrength = Pattern.compile("^(?=.*[`~!@#$%^&*()_+])(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,16}$").matcher(password).find();
            result.put("passwordStrength", checkStrength);
            return result;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }

    /**
     * 회원가입시 이름이 한국어이며 2글자 이상시에만 회원가입 가능
     * @param username 회원가입시 사용된 실명
     * @return  한국어로 작성된 이름시 true 그외 false
     */
    public Map<String, Boolean> userNameValid(String username) {
        try {
            Map<String, Boolean> result = new LinkedHashMap<>();

            boolean isKorean = Pattern.compile("^[가-힣]+.{1,}$").matcher(username).matches();
            // 한국어로 작성된 이름인 경우 true, 그 외에는 false
            result.put("isKorean", isKorean);
            return result;
        } catch (Exception e) {
            throw new RuntimeException();
        }
    }


    /**
     * 이메일주소가 올바른 주소인지 확인
     * @param email 회원가입시의 이메일정보
     * @return  올바른이메일 주소시 true, 아닐시 false
     */
    public Map<String, Boolean> emailValid(String email) {
        try {
            Map<String, Boolean> result = new LinkedHashMap<>();

            boolean valid = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$").matcher(email).matches();

            if (valid) {
                // 이메일이 데이터베이스에 존재하는지 확인
                List<Member> member = memberRepository.findByEmail(email);
                for (Member m : member) {
                    if (m.getLogin_type().equals("normal")) {
                        result.put("duplicateEmail",false);
                    } else {
                        result.put("duplicateEmail",true);
                    }
                }
                if (member.isEmpty()){
                    result.put("duplicateEmail",true);
                }
                result.put("useEmail",true);
            }else
                result.put("useEmail",false);

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

    /**
     회원가입 정보를 검증하는 메서드로, 사용자가 제출한 회원가입 폼의 데이터를 기반으로 각각의 조건을 검사하여 결과를 반환.
     반환된 결과는 각 검증 항목의 이름과 검증 결과로 이루어진 맵 형태로 제공한다.
     @param memberDto 검증할 회원 정보가 포함된 MemberDto 객체
     @return 각 검증 항목의 이름과 해당 검증 결과로 이루어진 맵
     */
    private Map<String, Boolean> validateSignUp(MemberDto memberDto,int code) {
        Map<String, Boolean> validationResult = new LinkedHashMap<>();

        validationResult.put("isLoginValid",isLoginIdValid(memberDto.getLoginId()));
        validationResult.putAll(duplicatePassword(memberDto.getPassword(), memberDto.getPasswordRe()));
        validationResult.putAll(checkPasswordStrength(memberDto.getPassword()));
        validationResult.putAll(userNameValid(memberDto.getUsername()));
        validationResult.putAll(emailValid(memberDto.getEmail()));
        validationResult.putAll(nickNameValid(memberDto.getNickName()));
        validationResult.putAll(verifyCode(memberDto.getEmail(),code));

        return validationResult;
    }

    /**
     * 회원가입시 이메일 인증번호 유호성 검사
     * @param code
     * @return 인증 성공시 true, 실패시 false
     */
    public Map<String, Boolean> verifyCode(String email, int code){
        Map<String, Boolean> stringBooleanMap = mailService.verifyCode(email,code);
        log.info("str={}",stringBooleanMap);
        return stringBooleanMap;
    }


}
