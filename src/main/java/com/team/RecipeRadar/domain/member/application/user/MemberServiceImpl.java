package com.team.RecipeRadar.domain.member.application.user;

import com.team.RecipeRadar.domain.email.dao.EmailVerificationRepository;
import com.team.RecipeRadar.domain.email.domain.EmailVerification;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.email.application.MailService;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notification.dao.NotificationRepository;
import com.team.RecipeRadar.domain.qna.dao.question.QuestionRepository;
import com.team.RecipeRadar.domain.member.dto.response.UserInfoResponse;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import com.team.RecipeRadar.global.auth.dao.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

    private final MemberRepository memberRepository;
    private final RefreshTokenRepository jwtRefreshTokenRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final NoticeRepository noticeRepository;
    private final NotificationRepository notificationRepository;
    private final SinUpService sinUpService;
    private final QuestionRepository questionRepository;

    @Qualifier("AccountEmail")
    private final MailService mailService;


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
     * 사용자의 정보를 탈퇴하는 메서드
     */
    public void deleteByLoginId(String loginId) {
        Member member = memberRepository.findByLoginId(loginId);
        deleteMemberId(member);
    }

    /**
     * 사용자의 정보(이름,이메일,닉네임,로그인 타입)등을 조회하는 메서드
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

        Map<String, Boolean> emailValid = sinUpService.emailValid(email);//이메일 유효성검사
        EmailVerification emailVerification = emailVerificationRepository.findByEmailAndCode(email, code);

        for (Map.Entry<String, Boolean> valid :emailValid.entrySet()) {
            if (valid.getValue() && emailVerification != null && emailVerification.expired(emailVerification)) {
                member.updateEmail(email);
                mailService.deleteCode(email,code);
            }else throw new InvalidIdException("인증번호 및 이메일이 잘못되었습니다.");
        }
    }

    /**
     * 회원 탈퇴 메서드
     */
    @Override
    public void deleteMember(Long memberId, boolean checkType) {
        Member member = getMember(memberId);
        validateNormalUser(member);

        if (!checkType)
            throw new InvalidIdException("약관 동의를 주세요.");

       deleteMemberId(member);
    }

    /* 사용자 정보 조회 */

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));
    }
    /* 일반 사용자 인지 검증 메서드*/

    private static void validateNormalUser(Member member) {
        if (!member.getLogin_type().equals("normal"))
            throw new AccessDeniedException("일반 사용자만 가능합니다.");
    }
    private void deleteMemberId(Member member) {
        noticeRepository.deleteMemberId(member.getId());
        notificationRepository.deleteMember(member.getId());
        jwtRefreshTokenRepository.DeleteByMemberId(member.getId());
        questionRepository.deleteAllByMemberId(member.getId());
        memberRepository.delete(member);
    }
}
