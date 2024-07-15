package com.team.RecipeRadar.domain.member.application.admin;

import com.team.RecipeRadar.domain.blackList.dto.response.MemberInfoResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminMemberService {

    /* 사용자 모두 조회*/
    long searchAllMembers();

    /* 사용자 정보 페이징*/
    MemberInfoResponse memberInfos(Long lastMemberId, Pageable pageable);

    /* 사용자 추방 */
    List<String> adminDeleteUsers(List<Long> memberIds);

    /* 사용자 검색 */
    MemberInfoResponse searchMember(String loginId, String nickname, String email, String username,Long lastMemberId,Pageable pageable);

}
