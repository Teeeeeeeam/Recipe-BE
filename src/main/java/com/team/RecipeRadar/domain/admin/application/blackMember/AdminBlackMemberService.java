package com.team.RecipeRadar.domain.admin.application.blackMember;

import com.team.RecipeRadar.domain.admin.dto.BlackListResponse;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminBlackMemberService{

    /* 사용자 모두 조회*/
    long searchAllMembers();

    /* 블랙리스트 조회*/
    BlackListResponse getBlackList(Long lastId, Pageable pageable);

    /* 블랙리스트 임시 차단 여부*/
    boolean temporarilyUnblockUser(Long blackId);
    
    /* 사용자 정보 페이징*/
    MemberInfoResponse memberInfos(Long lastMemberId, Pageable pageable);

    /* 사용자 추방 */
    List<String> adminDeleteUsers(List<Long> memberIds);

    /* 사용자 검색 */
    MemberInfoResponse searchMember(String loginId, String nickname, String email, String username,Long lastMemberId,Pageable pageable);

    /* 블랙 리스트 삭제*/
    void deleteBlackList(Long blackId);

    /* 블랙 리스트 이메일 조회 */
    BlackListResponse searchEmailBlackList(String email,Long lastId,Pageable pageable);
}
