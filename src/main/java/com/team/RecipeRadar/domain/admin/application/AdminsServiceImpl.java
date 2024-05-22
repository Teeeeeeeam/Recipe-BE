package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.dao.BlackList;
import com.team.RecipeRadar.domain.admin.domain.BlackListRepository;
import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.recipe.dao.bookmark.RecipeBookmarkRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.global.jwt.Entity.RefreshToken;
import com.team.RecipeRadar.global.jwt.repository.JWTRefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminsServiceImpl implements AdminService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final RecipeRepository recipeRepository;
    private final NoticeRepository noticeRepository;
    private final RecipeBookmarkRepository recipeBookmarkRepository;
    private final JWTRefreshTokenRepository jwtRefreshTokenRepository;
    private final BlackListRepository blackListRepository;


    @Override
    public long searchAllMembers() {
        return memberRepository.countAllBy();
    }

    @Override
    public long searchAllPosts() {
        return postRepository.countAllBy();
    }

    @Override
    public long searchAllRecipes() {
        return recipeRepository.countAllBy();
    }

    @Override
    public MemberInfoResponse memberInfos(Pageable pageable) {
        Slice<Member> memberInfo = memberRepository.getMemberInfo(pageable);
        List<MemberDto> memberDtoList = memberInfo.getContent().stream().map(m -> MemberDto.of(m.getId(), m.getLoginId(), m.getEmail(), m.getUsername(), m.getNickName(), m.getJoin_date())).collect(Collectors.toList());
        boolean hasNext = memberInfo.hasNext();
        MemberInfoResponse memberInfoResponse = new MemberInfoResponse(memberDtoList, hasNext);
        return memberInfoResponse;
    }

    @Override
    public void adminDeleteUser(Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchElementException("사용자를 찾을수 없습니다."));

        boolean existsByEmail = blackListRepository.existsByEmail(member.getEmail());
        if (!existsByEmail) {
            BlackList blackList = BlackList.toEntity(member.getEmail());
            blackListRepository.save(blackList);
        }

        Long save_memberId = member.getId();
        noticeRepository.deleteByMember_Id(save_memberId);
        recipeBookmarkRepository.deleteByMember_Id(save_memberId);
        jwtRefreshTokenRepository.DeleteByMemberId(save_memberId);
        memberRepository.deleteById(save_memberId);

    }
}
