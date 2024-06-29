package com.team.RecipeRadar.global.auth.application;

import com.team.RecipeRadar.domain.account.dao.AccountRetrievalRepository;
import com.team.RecipeRadar.domain.account.domain.AccountRetrieval;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ex.InvalidIdException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import com.team.RecipeRadar.global.auth.dto.response.MemberInfoResponse;
import com.team.RecipeRadar.global.security.jwt.provider.JwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRetrievalRepository accountRetrievalRepository;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    /**
     * 엑세스 토큰을 통해 사용자의 정보를 조회한다.
     * @param accessToken   헤더로 요청된 accessToken값
     * @return MemberInfoResponse 객체를 반환
     */
    @Override
    public MemberInfoResponse accessTokenMemberInfo(String accessToken) {
        String loginId = jwtProvider.validateAccessToken(accessToken);
        Member member = memberRepository.findByLoginId(loginId);
        if(member!=null){
            return MemberInfoResponse.of(MemberDto.from(member));
        }else
            throw new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER);
    }

    /**
     * 일반 사용자 페이지 접근시 비밀번호 재 검증후 쿠키생성
     */
    @Override
    public String userToken(Long memberId, String password) {
        Member member = getMember(memberId);

        validPassword(password, member);

        return accountRetrievalRepository.save(AccountRetrieval.createAccount(member.getLoginId(),20)).getVerificationId();
    }

    /* 비밀번호 검증 테스트 */
    private void validPassword(String password, Member member) {
        if (member.getLogin_type().equals("normal")) {
            boolean matches = passwordEncoder.matches(password, member.getPassword());
            if (!matches) {
                throw new InvalidIdException("비밀번호가 일치하지 않습니다.");
            }
        }
    }

    /* 사용자 정보 조회 */
    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));
    }

}
