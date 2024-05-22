package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.admin.dto.MemberInfoResponse;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AdminsServiceImpl implements AdminService {

    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final RecipeRepository recipeRepository;


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
}
