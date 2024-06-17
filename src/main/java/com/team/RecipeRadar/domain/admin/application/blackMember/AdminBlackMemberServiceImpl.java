package com.team.RecipeRadar.domain.admin.application.blackMember;

import com.team.RecipeRadar.domain.admin.dao.BlackList;
import com.team.RecipeRadar.domain.admin.domain.BlackListRepository;
import com.team.RecipeRadar.domain.admin.dto.BlackListDto;
import com.team.RecipeRadar.domain.admin.dto.BlackListResponse;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.application.MemberService;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.exception.ex.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.NoSuchErrorType;
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
public class AdminBlackMemberServiceImpl implements AdminBlackMemberService {

    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final BlackListRepository blackListRepository;

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


    /**
     * 블랙리스트 저장된 모든 정보를 조회하는 메서드
     */
    @Override
    public BlackListResponse getBlackList(Long lastId, Pageable pageable) {
        Slice<BlackListDto> blackListDtoList = blackListRepository.allBlackList(lastId, pageable);
        return new BlackListResponse(blackListDtoList.hasNext(),blackListDtoList.getContent());
    }

    /**
     * 블랙리스트 임시 차단 메서드
     * 블랙리스트의 대해서 임시적으로 관리자가 차단/해제를 할수 있습니다.
     */
    @Override
    public boolean temporarilyUnblockUser(Long blackId) {
        BlackList blackList = blackListRepository.findById(blackId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_EMAIL));
        blackList.unLock(blackList.isBlack_check());
        BlackList update_black = blackListRepository.save(blackList);

        return update_black.isBlack_check();
    }

    /**
     * 블랙리스트를 해제하는 메서드
     */
    @Override
    public void deleteBlackList(Long blackId) {
        blackListRepository.deleteById(blackId);
    }

    /**
     * 블랙리스트된 이메일을 조회하는 메서드
     */
    @Override
    @Transactional(readOnly = true)
    public BlackListResponse searchEmailBlackList(String email, Long lastId, Pageable pageable) {
        Slice<BlackListDto> searchEmailBlackList = blackListRepository.searchEmailBlackList(email, lastId, pageable);
        return new BlackListResponse(searchEmailBlackList.hasNext(),searchEmailBlackList.getContent());
    }

    /* 사용자를 삭제 시킬때 관련된 모든 정보를 삭제하는 메서드 */
    private void deleteMemberRelatedData(String loginId) {
        memberService.deleteMember(loginId);
    }
}
