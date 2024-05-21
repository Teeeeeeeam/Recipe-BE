package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
