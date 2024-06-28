package com.team.RecipeRadar.domain.member.application.user;

import com.team.RecipeRadar.domain.blackList.dao.BlackListRepository;
import com.team.RecipeRadar.domain.email.application.MailService;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class SinUpServiceImpl implements SinUpService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final BlackListRepository blackListRepository;

    @Qualifier("JoinEmail")
    private final MailService mailService;

    private static final String DUPLICATE_EMAIL = "duplicateEmail";
    private static final String EMAIL = "email";
    private static final String USE_LOGIN_ID = "useLoginId";
    private static final String PASSWORD_RE = "passwordRe";
    private static final String LOGIN_ID = "loginId";
    private static final String CODE = "code";
    private static final String DUPLICATE_PASSWORD = "duplicate_password";
    private static final String IS_LOGIN_VALID = "isLoginValid";
    private static final String USE_EMAIL = "useEmail";
    private static final String BLACKLIST_EMAIL = "blackListEmail";
    private static final String NORMAL_LOGIN_TYPE = "normal";

    @Override
    public void joinMember(MemberDto memberDto) {
        memberDto.setPassword(passwordEncoder.encode(memberDto.getPassword()));
        Member entity = MemberDto.toEntity(memberDto);
        memberRepository.save(entity);
    }


    /**
     * 회원가입시 모든 조건검사를 하지 않았을때 회원가입을 하지 못함.
     * @param memberDto 회원가입의 정보
     * @return  모두 사용시 true, 하나라도 검사 안했을시 false
     */
    @Override
    public boolean ValidationOfSignUp(MemberDto memberDto) {
        Map<String, Boolean> validationResult = validateSignUp(memberDto);

        for (Map.Entry<String, Boolean> entry : validationResult.entrySet()) {
            if (!entry.getValue()) {
                log.error("Failed validation: {}", entry.getKey());
                return false;
            }
        }
        mailService.deleteCode(memberDto.getEmail(), memberDto.getCode());
        return true;
    }

    /**
     * 회원가입시 아이디 중복검사 및 아이디 조건체크(대소문자 구문)
     * @param loginId 회원가입시 정보
     * @return  아이디가 이미 사용 중이거나 조건이 불충분하면 false를 반환하고, 모두 만족시 true를 반환합니다
     */
    @Override
    public Map<String, Boolean> LoginIdValid(String loginId) {
        if (isLoginIdDuplicated(loginId) || memberRepository.findByCaseSensitiveLoginId(loginId) != null) {
            throw new InvalidIdException("사용할 수 없는 아이디입니다.");
        }
        return Map.of(USE_LOGIN_ID, true);
    }

    /**
     * 회원가입시 닉네임이 유효한지 확인합니다.
     * @param nickname 회원가입시 사용할 닉네임
     * @return 닉네임이 유효할 경우 true, 그렇지 않을 경우 false
     */
    @Override
    public void nickNameValid(String nickname) {
        boolean isNicknameValid = Pattern.compile("^[a-zA-Z0-9가-힣]{4,12}$").matcher(nickname).matches();
        if (!isNicknameValid || memberRepository.existsByNickName(nickname)) {
            throw new InvalidIdException("사용 불가능한 닉네임입니다.");
        }
    }

    /**
     * 회원가입시 오류 메시지를 추출하는 메서드
     * 비밀번호 중복검사, 이메일 중복검사, 아이디 중복검사를 최종적으로 한번 더하는 메서드
     */
    @Override
    public Map<String,String> ValidationErrorMessage(MemberDto memberDto) {
        Map<String, Boolean> validationResult = validateSignUp(memberDto);
        Map<String, String> errorMessages = new HashMap<>();

        validationResult.forEach((key, value) -> {
            if (!value) {
                switch (key) {
                    case DUPLICATE_PASSWORD:
                        errorMessages.put(PASSWORD_RE, "비밀번호가 일치하지 않습니다.");
                        break;
                    case IS_LOGIN_VALID:
                        errorMessages.put(LOGIN_ID, "중복된 아이디입니다.");
                        break;
                    case DUPLICATE_EMAIL:
                        errorMessages.put(EMAIL, "중복된 이메일입니다.");
                        break;
                    default:
                        errorMessages.put(CODE, "인증 번호가 일치하지 않습니다.");
                }
            }
        });

        return errorMessages;
    }

    /**
     회원가입 정보를 검증하는 메서드로, 사용자가 제출한 회원가입 폼의 데이터를 기반으로 각각의 조건을 검사하여 결과를 반환.
     반환된 결과는 각 검증 항목의 이름과 검증 결과로 이루어진 맵 형태로 제공한다.
     @param memberDto 검증할 회원 정보가 포함된 MemberDto 객체
     @return 각 검증 항목의 이름과 해당 검증 결과로 이루어진 맵
     */
    private Map<String, Boolean> validateSignUp(MemberDto memberDto) {
        Map<String, Boolean> validationResult = new LinkedHashMap<>();
        validationResult.put(IS_LOGIN_VALID,!isLoginIdDuplicated(memberDto.getLoginId()));
        validationResult.putAll(duplicatePassword(memberDto.getPassword(), memberDto.getPasswordRe()));
        validationResult.put(DUPLICATE_EMAIL,!isDuplicateEmail(memberDto.getEmail()));
        validationResult.putAll(emailValid(memberDto.getEmail()));
        validationResult.putAll(verifyCode(memberDto.getEmail(), memberDto.getCode()));

        return validationResult;
    }

    /**
     * 회원가입시 이메일 인증번호 유호성 검사
     * @param code
     * @return 인증 성공시 true, 실패시 false
     */
    private Map<String, Boolean> verifyCode(String email, int code){
        return mailService.verifyCode(email,code);
    }


    /**
     * 이메일주소가 올바른 주소인지 확인
     * @param email 회원가입시의 이메일정보
     * @return  올바른이메일 주소시 true, 아닐시 false
     */
    @Override
    public Map<String, Boolean> emailValid(String email) {
        Map<String, Boolean> result = new LinkedHashMap<>();

        boolean isBlackListed = blackListRepository.existsByEmail(email);
        boolean isValidFormat =  Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.(com|net)$").matcher(email).matches();
        boolean isDuplicateEmail = isValidFormat && !isBlackListed && !isDuplicateEmail(email);
        boolean canUseEmail = isValidFormat && !isBlackListed;

        result.put(DUPLICATE_EMAIL, isDuplicateEmail);
        result.put(USE_EMAIL, canUseEmail);
        result.put(BLACKLIST_EMAIL, !isBlackListed);

        return result;
    }




    /**
     * 주어진 회원 정보를 통해 비밀번호 중복여부를 체크하고, 결과를 반환합니다.
     * @param password 첫번쨰 비밀번호
     * @param passwordRe 다시 입력한 비밀번호
     * @return 비밀번호 일치여부를 Map<String,Boolean>타입으로 반환
     */
    public Map<String, Boolean> duplicatePassword(String password,String passwordRe) {
        Map<String, Boolean> result = new LinkedHashMap<>();

        result.put(DUPLICATE_PASSWORD, password.equals(passwordRe));
        return result;
    }
    /**
     * 이메일을 중복검사하는 메서드
     * 일반 사용자의 이메일의 대해서만 검사한다.
     */
    private boolean isDuplicateEmail(String email) {
        return memberRepository.findByEmail(email).stream()
                .anyMatch(member ->NORMAL_LOGIN_TYPE.equals(member.getLogin_type()));
    }

    /**
     * 아이디 조건검사 중복검사
     */
    private boolean isLoginIdDuplicated(String loginId) {
        return  memberRepository.existsByLoginId(loginId);
    }

}
