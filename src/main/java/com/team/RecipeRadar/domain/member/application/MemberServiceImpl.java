package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.admin.domain.BlackListRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.email.application.MailService;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notification.dao.NotificationRepository;
import com.team.RecipeRadar.domain.questions.dao.QuestionRepository;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JWTRefreshTokenRepository jwtRefreshTokenRepository;
    private final BlackListRepository blackListRepository;
    private final NoticeRepository noticeRepository;
    private final NotificationRepository notificationRepository;
    private final QuestionRepository questionRepository;

    @Qualifier("JoinEmail")
    private final MailService mailService;

    @Override
    public void joinMember(MemberDto memberDto) {
        Member entity = MemberDto.toEntity(memberDto,passwordEncoder);
        memberRepository.save(entity);
    }

    /**
     * 회원가입시 아이디 중복검사 및 아이디 조건체크(대소문자 구문)
     * @param loginId 회원가입시 정보
     * @return  아이디가 이미 사용 중이거나 조건이 불충분하면 false를 반환하고, 모두 만족시 true를 반환합니다
     */
    public Map<String, Boolean> LoginIdValid(String loginId) {
        Map<String, Boolean> result = new LinkedHashMap<>();
            boolean loginIdDuplicated = isLoginIdDuplicated(loginId);
            Member member = memberRepository.findByCaseSensitiveLoginId(loginId);
            if (member==null && !loginIdDuplicated) {
                result.put("useLoginId", true);
            }else
                throw new InvalidIdException("사용할수 없는 아이디입니다.");
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

        result.put("duplicate_password", password.equals(passwordRe));
        return result;
    }

    /**
     * 회원가입시 닉네임이 유효한지 확인합니다.
     * @param nickname 회원가입시 사용할 닉네임
     * @return 닉네임이 유효할 경우 true, 그렇지 않을 경우 false
     */
    @Override
    public void nickNameValid(String nickname) {
        Boolean existsByNickName = memberRepository.existsByNickName(nickname);

        boolean valid = Pattern.compile("^[a-zA-Z0-9가-힣]{4,12}$").matcher(nickname).matches();
        if(!valid || existsByNickName) throw  new InvalidIdException("사용 불가능한 닉네임 입니다.");
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
     * 강력한 비밀번호인지 확인 "특수문자","0~9","소문자","대문자","8자리이상" 포함 여부 확인
     * @param password 회원가입시 사용자정보
     * @return 강력한 비밀번호시 true, 아닐시 false 반환
     */
    public Map<String, Boolean> checkPasswordStrength(String password) {
        Map<String, Boolean> result = new LinkedHashMap<>();
        // 특수 문자 포함 여부, 대문자 알파벳 포함 여부, 비밀번호의 길이가 8자리 이상인지를 한 번에 확인
        boolean checkStrength = Pattern.compile("^(?=.*[`~!@#$%^&*()_+])(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,16}$").matcher(password).find();
        result.put("passwordStrength", checkStrength);
        return result;
    }

    /**
     * 이메일주소가 올바른 주소인지 확인
     * @param email 회원가입시의 이메일정보
     * @return  올바른이메일 주소시 true, 아닐시 false
     */
    public Map<String, Boolean> emailValid(String email) {

        Map<String, Boolean> result = new LinkedHashMap<>();

        boolean blackList = blackListRepository.existsByEmail(email);

        boolean valid = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.(com|net)$").matcher(email).matches();
        boolean duplicateEmail = true;
        boolean useEmail = true;

        if (valid && !blackList) {
            // 이메일이 데이터베이스에 존재하는지 확인
            duplicateEmail = isDuplicateEmail(email);
        }else
            useEmail = false;

        result.put("duplicateEmail",duplicateEmail);
        result.put("useEmail",useEmail);
        return result;
    }

    /**
     회원가입 정보를 검증하는 메서드로, 사용자가 제출한 회원가입 폼의 데이터를 기반으로 각각의 조건을 검사하여 결과를 반환.
     반환된 결과는 각 검증 항목의 이름과 검증 결과로 이루어진 맵 형태로 제공한다.
     @param memberDto 검증할 회원 정보가 포함된 MemberDto 객체
     @return 각 검증 항목의 이름과 해당 검증 결과로 이루어진 맵
     */
    private Map<String, Boolean> validateSignUp(MemberDto memberDto) {
        Map<String, Boolean> validationResult = new LinkedHashMap<>();
        validationResult.put("isLoginValid",!isLoginIdDuplicated(memberDto.getLoginId()));
        validationResult.putAll(duplicatePassword(memberDto.getPassword(), memberDto.getPasswordRe()));
        validationResult.put("duplicateEmail",!isDuplicateEmail(memberDto.getEmail()));
        validationResult.putAll(emailValid(memberDto.getEmail()));
        validationResult.putAll(verifyCode(memberDto.getEmail(), memberDto.getCode()));

        return validationResult;
    }

    /**
     * 회원가입시 이메일 인증번호 유호성 검사
     * @param code
     * @return 인증 성공시 true, 실패시 false
     */
    @Override
    public Map<String, Boolean> verifyCode(String email, int code){
        Map<String, Boolean> stringBooleanMap = mailService.verifyCode(email,code);
        return stringBooleanMap;
    }

    /**
     * 사용자의 정보를 탈퇴하는 메서드
     */
    @Override
    public void deleteMember(String loginId) {
        Member member = memberRepository.findByLoginId(loginId);
        noticeRepository.deleteMemberId(member.getId());
        notificationRepository.deleteMember(member.getId());
        jwtRefreshTokenRepository.DeleteByMemberId(member.getId());
        questionRepository.deleteAllByMemberId(member.getId());
        memberRepository.delete(member);
    }

    /**
     * 회원가입시 오류 메시지를 추출하는 메서드
     * 비밀번호 중복검사, 이메일 중복검사, 아이디 중복검사를 최종적으로 한번 더하는 메서드
     */
    @Override
    public Map<String,String> ValidationErrorMessage(MemberDto memberDto) {
        Map<String, Boolean> validationResult = validateSignUp(memberDto);

        Map<String,String> result = new HashMap<>();

        for (Map.Entry<String, Boolean> entry : validationResult.entrySet()){
            if(!entry.getValue()){
                String key = entry.getKey();
                if(key.equals("duplicate_password"))
                    result.put("passwordRe","비밀 번호가 일치하지 않습니다.");
                else if(key.equals("isLoginValid")){
                    result.put("loginId","중복된 아이디 입니다.");
                }else if(key.equals("duplicateEmail")){
                    result.put("email","종복된 이메일 입니다.");
                } else
                    result.put("code","인증 번호가 일치하지 않습니다.");
            }
        }
        return result;
    }

    @Override
    public void emailValidCon(String email) {
        boolean duplicateEmail = isDuplicateEmail(email);

        boolean valid = Pattern.compile("^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.(com|net)$").matcher(email).matches();
        if(!duplicateEmail || !valid)
            throw new InvalidIdException("이메일 사용 불가능");
    }

    /**
     * 이메일을 중복검사하는 메서드
     * 일반 사용자의 이메일의 대해서만 검사한다.
     */
    private boolean isDuplicateEmail(String email) {
        List<Member> member = memberRepository.findByEmail(email);
        for (Member m : member) {
            if (m.getLogin_type().equals("normal")) {
                return  false;
            }
        }
        return true;
    }

    /**
     * 아이디 조건검사 중복검사
     * @param loginId
     * @return 대문자나 소문자일시 true, 아닐시 false
     */
    private boolean isLoginIdDuplicated(String loginId) {
        return  memberRepository.existsByLoginId(loginId);
    }
}
