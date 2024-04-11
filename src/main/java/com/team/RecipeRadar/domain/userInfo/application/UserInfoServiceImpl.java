package com.team.RecipeRadar.domain.userInfo.application;

import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.userInfo.dto.info.UserInfoResponse;
import com.team.RecipeRadar.global.email.application.MailService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {

    private final MemberRepository memberRepository;

    @Qualifier("AccountEmail")
    private final MailService mailService;
    private final MemberService memberService;


    /**
     * 사용자의 정보(이름,이메일,닉네임,로그인 타입)등을 조회하는 로직
     * @param loginId   로그인아이디
     * @param authName  시큐리티 컨텍스트의 저장된 이름
     * @return  userInfoResponse 데이터 반환
     */
    @Override
    public UserInfoResponse getMembers(String loginId,String authName) {
        Member member = throwsMember(loginId, authName);

        UserInfoResponse userInfoResponse = UserInfoResponse.builder()
                .nickName(member.getNickName())
                .username(member.getUsername())
                .email(member.getEmail())
                .loginType(member.getLogin_type()).build();

        return userInfoResponse;
    }

    /**
     * 사용자 닉네임 변경 
     * @param nickName  변경할 닉네임
     * @param loginId   로그안한 아이디
     * @param authName  로그인한 사용자 이름
     */
    public void updateNickName(String nickName,String loginId,String authName){

        Member member = throwsMember(loginId, authName);

        member.updateNickName(nickName);

        memberRepository.save(member);
    }

    /**
     *  사용자의 이메일 변경 
     * @param email     변경할 이메일
     * @param code      이메일 인증번호
     * @param loginId   로그인 아이디
     * @param authName  로그인한 사용자 정보
     */
    @Override
    public void updateEmail(String email, String code, String loginId, String authName) {

        Member member = throwsMember(loginId, authName);
        Map<String, Boolean> emailValid = memberService.emailValid(email);      //이메일 유효성검사
        Boolean duplicateEmail = emailValid.get("duplicateEmail");
        Boolean useEmail = emailValid.get("useEmail");
        Map<String, Boolean> verifyCode = memberService.verifyCode(email, Integer.parseInt(code));      //이메일 인증 코드 검사
        Boolean aBoolean = verifyCode.get("isVerifyCode");

        if (aBoolean&&duplicateEmail&&useEmail){
            member.updateEmail(email);
            memberRepository.save(member);
            mailService.deleteCode(email,Integer.parseInt(code));
        }else
            throw new BadRequestException("인증번호 및 이메일이 잘못되었습니다.");
    }

    private Member throwsMember(String loginId, String authName) {
        Member member = memberRepository.findByLoginId(loginId);

        if (member == null || !member.getUsername().equals(authName)) {
            throw new AccessDeniedException("잘못된 접근입니다.");
        }
        return member;
    }
}
