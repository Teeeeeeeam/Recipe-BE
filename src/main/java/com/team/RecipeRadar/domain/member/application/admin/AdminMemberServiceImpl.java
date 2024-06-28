package com.team.RecipeRadar.domain.member.application.admin;

import com.team.RecipeRadar.domain.blackList.dto.response.MemberInfoResponse;
import com.team.RecipeRadar.domain.blackList.domain.BlackList;
import com.team.RecipeRadar.domain.blackList.dao.BlackListRepository;
import com.team.RecipeRadar.domain.member.application.user.MemberService;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminMemberServiceImpl implements AdminMemberService{

    private final MemberRepository memberRepository;
    private final BlackListRepository blackListRepository;
    private final MemberService memberService;
    /**
     * 사용자 수를 조회하는 메서드
     */
    @Override
    public long searchAllMembers() {
        return memberRepository.countAllBy();
    }


    /**
     * 사용자의 정보를 모두 조회하는 메서드
     * 무한페이징 방식을 지원합니다.
     */
    @Override
    public MemberInfoResponse memberInfos(Long lastMemberId,Pageable pageable) {
        Slice<MemberDto> memberInfo = memberRepository.getMemberInfo(lastMemberId,pageable);
        boolean hasNext = memberInfo.hasNext();
        return new MemberInfoResponse(memberInfo.getContent(), hasNext);
    }

    /**
     * 회원 탈퇴 메서드
     * 어드민 사용자는 회원을 강제로 삭제 시키며, 블랙리스트에 추가됩니다.
     */
    @Override
    public List<String> adminDeleteUsers(List<Long> memberIds) {
        List<String> deletedEmails = new ArrayList<>();

        for (Long memberId : memberIds) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));
            // 블랙리스트에 추가
            if (!blackListRepository.existsByEmail(member.getEmail())) {
                BlackList blackList = BlackList.toEntity(member.getEmail());
                blackListRepository.save(blackList);
                deletedEmails.add(member.getEmail());
            }
            // 사용자와 관련된 데이터 삭제
            deleteMemberRelatedData(member.getLoginId());
        }

        return deletedEmails;
    }

    /**
     * 사용자를 검색하는 메서드
     * 사용자의 대해서 로그인아이디, 닉네임, 실명, 사용자 번호 를사용해 사용자를 검색 할 수 있습니다.
     */
    @Override
    public MemberInfoResponse searchMember(String loginId, String nickname, String email, String username,Long lastMemberId,Pageable pageable) {
        Slice<MemberDto> memberDtoSlice = memberRepository.searchMember(loginId, nickname, email, username, lastMemberId,pageable);
        return new MemberInfoResponse(memberDtoSlice.getContent(),memberDtoSlice.hasNext());
    }

    /* 사용자를 삭제 시킬때 관련된 모든 정보를 삭제하는 메서드 */
    private void deleteMemberRelatedData(String loginId) {
        memberService.deleteByLoginId(loginId);
    }

}
