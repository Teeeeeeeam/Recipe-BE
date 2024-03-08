package com.team.RecipeRadar.Service.impl;

import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.Service.MemberService;
import com.team.RecipeRadar.dto.MemberDto;
import com.team.RecipeRadar.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    private final JoinEmailServiceImplV1 joinEmailServiceImplV1;

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
     * @param memberDto 회원가입시 정보
     * @return  아이디가 이미 사용 중이거나 조건이 불충분하면 false를 반환하고, 모두 만족시 true를 반환합니다
     */
    public Map<String, Boolean> LoginIdValid(MemberDto memberDto) {
        Map<String, Boolean> result = new LinkedHashMap<>();
        try {
            boolean isLoginIdValid = isLoginIdValid(memberDto);
            Member member = memberRepository.findByCaseSensitiveLoginId(memberDto.getLoginId());
            result.put("use_loginId", member==null && isLoginIdValid);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ServerErrorException("오류");
        }
    }

    /**
     * 아이디 조건검사 대문자나 소문자(5~16)자리 입력시 사용할수있다.
     * @param memberDto
     * @return 대문자나 소문자일시 true, 아닐시 false
     */
    private boolean isLoginIdValid(MemberDto memberDto) {
        String loginId = memberDto.getLoginId();
        Member member = memberRepository.findByCaseSensitiveLoginId(loginId);

        boolean  pattern= Pattern.compile("^[a-zA-Z0-9]{5,16}$").matcher(loginId).find();
        if (member!=null||!pattern) {
            return false;
        }

        return true;
    }

    /**
     * 주어진 회원 정보를 통해 비밀번호 중복여부를 체크하고, 결과를 반환합니다.
     * @param memberDto 회원 정보를 담은 Dto
     * @return 비밀번호 일치여부를 Map<String,Boolean>타입으로 반환
     */
    public Map<String, Boolean> duplicatePassword(MemberDto memberDto) {
        Map<String, Boolean> result = new LinkedHashMap<>();
        String password = memberDto.getPassword();          //첫번째 비밀번호
        String passwordRe = memberDto.getPasswordRe();      //두번째 비밀번호

        result.put("duplicate_password", password.equals(passwordRe));
        return result;
    }

    /**
     * 회원가입시 닉네임이 유효한지 확인합니다.
     * @param memberDto 회원가입 정보
     * @return 닉네임이 유효할 경우 true, 그렇지 않을 경우 false
     */
    @Override
    public Map<String, Boolean> nickNameValid(MemberDto memberDto) {
        try{
            Map<String, Boolean> result = new LinkedHashMap<>();
            String nickName = memberDto.getNickName();

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
    public boolean ValidationOfSignUp(MemberDto memberDto,String code) {
        Map<String, Boolean> validationResult = validateSignUp(memberDto,code);

        for (Map.Entry<String, Boolean> entry : validationResult.entrySet()) {
            if (!entry.getValue()) {
                log.error("Failed validation: {}", entry.getKey());
                return false;
            }
        }
        return true;
    }

    /**
     * 강력한 비밀번호인지 확인 "특수문자","0~9","소문자","대문자","8자리이상" 포함 여부 확인
     * @param memberDto 회원가입시 사용자정보
     * @return 강력한 비밀번호시 true, 아닐시 false 반환
     */
    public Map<String, Boolean> checkPasswordStrength(MemberDto memberDto) {
        try {
            String password = memberDto.getPassword();

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
     * @param memberDto 회원가입 정보
     * @return  한국어로 작성된 이름시 true 그외 false
     */
    public Map<String, Boolean> userNameValid(MemberDto memberDto) {
        try {
            Map<String, Boolean> result = new LinkedHashMap<>();
            String username = memberDto.getUsername();

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
     * @param memberDto 회원가입시의 이메일정보
     * @return  올바른이메일 주소시 true, 아닐시 false
     */
    public Map<String, Boolean> emailValid(MemberDto memberDto) {
        try {
            Map<String, Boolean> result = new LinkedHashMap<>();
            String email = memberDto.getEmail();

            boolean valid = Pattern.compile("^[a-zA-Z0-9]+@[a-zA-Z]+\\.(com|net)$").matcher(email).matches();

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
    private Map<String, Boolean> validateSignUp(MemberDto memberDto,String code) {
        Map<String, Boolean> validationResult = new LinkedHashMap<>();

        validationResult.put("isLoginValid",isLoginIdValid(memberDto));
        validationResult.putAll(duplicatePassword(memberDto));
        validationResult.putAll(checkPasswordStrength(memberDto));
        validationResult.putAll(userNameValid(memberDto));
        validationResult.putAll(emailValid(memberDto));
        validationResult.putAll(nickNameValid(memberDto));
        validationResult.putAll(verifyCode(code));

        return validationResult;
    }

    /**
     * 회원가입시 이메일 인증번호 유호성 검사
     * @param code
     * @return 인증 성공시 true, 실패시 false
     */
    public Map<String, Boolean> verifyCode(String code){
        Map<String, Boolean> result = new LinkedHashMap<>();
        String realCode = getCode();
        if (realCode.equals(code)){
            result.put("isVerifyCode",true);
        }else result.put("isVerifyCode",false);

        return result;
    }

    private String getCode(){
        return joinEmailServiceImplV1.getCode();
    }
}
