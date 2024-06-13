package com.team.RecipeRadar.domain.admin.application.blackMember;

import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.admin.dao.BlackList;
import com.team.RecipeRadar.domain.admin.domain.BlackListRepository;
import com.team.RecipeRadar.domain.admin.dto.BlackListDto;
import com.team.RecipeRadar.domain.admin.dto.BlackListResponse;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.recipe.dao.bookmark.RecipeBookmarkRepository;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminBlackMemberServiceImpl implements AdminBlackMemberService {

    private final MemberRepository memberRepository;
    private final BlackListRepository blackListRepository;
    private final CommentRepository commentRepository;
    private final JWTRefreshTokenRepository jwtRefreshTokenRepository;
    private final ImgRepository imgRepository;
    private final RecipeBookmarkRepository recipeBookmarkRepository;

    @Override
    public long searchAllMembers() {
        return memberRepository.countAllBy();
    }


    @Override
    public MemberInfoResponse memberInfos(Long lastMemberId,Pageable pageable) {
        Slice<MemberDto> memberInfo = memberRepository.getMemberInfo(lastMemberId,pageable);
        boolean hasNext = memberInfo.hasNext();
        MemberInfoResponse memberInfoResponse = new MemberInfoResponse(memberInfo.getContent(), hasNext);
        return memberInfoResponse;
    }

    /**
     * 여러명의 회원을 한번에 삭제 시킨다.
     * @param memberIds
     */
    @Override
    public List<String> adminDeleteUsers(List<Long> memberIds) {

        List<String> emailList = new ArrayList<>();

        for (Long memberId : memberIds) {
            Member member =memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("사용자를 찾을수 없습니다."));
            boolean existsByEmail = blackListRepository.existsByEmail(member.getEmail());
            if (!existsByEmail) {
                BlackList blackList = BlackList.toEntity(member.getEmail());
                emailList.add(member.getEmail());
                blackListRepository.save(blackList);
            }

            Long save_memberId = member.getId();
            commentRepository.deleteMember_comment(save_memberId);
            imgRepository.deleteMemberImg(save_memberId);
            recipeBookmarkRepository.deleteByMember_Id(save_memberId);
            jwtRefreshTokenRepository.DeleteByMemberId(save_memberId);
            memberRepository.deleteById(save_memberId);
        }

        return emailList;
    }

    @Override
    public MemberInfoResponse searchMember(String loginId, String nickname, String email, String username,Long lastMemberId,Pageable pageable) {
        Slice<MemberDto> memberDtoSlice = memberRepository.searchMember(loginId, nickname, email, username, lastMemberId,pageable);
        return new MemberInfoResponse(memberDtoSlice.getContent(),memberDtoSlice.hasNext());
    }


    /**
     * 블랙리스트 무한 페이징
     */
    @Override
    public BlackListResponse getBlackList(Long lastId, Pageable pageable) {
        Slice<BlackListDto> blackListDtoList = blackListRepository.allBlackList(lastId, pageable);
        return new BlackListResponse(blackListDtoList.hasNext(),blackListDtoList.getContent());
    }

    @Override
    public boolean temporarilyUnblockUser(Long blackId) {
        BlackList blackList = blackListRepository.findById(blackId).orElseThrow(() -> new BadRequestException("이메일이 존재하지 않습니다."));
        blackList.unLock(blackList.isBlack_check());
        BlackList update_black = blackListRepository.save(blackList);

        return update_black.isBlack_check();
    }
    @Override
    public void deleteBlackList(Long blackId) {
        blackListRepository.deleteById(blackId);
    }

}
