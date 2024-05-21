package com.team.RecipeRadar.domain.admin.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminsServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock RecipeRepository recipeRepository;
    @Mock PostRepository postRepository;

    @InjectMocks AdminsServiceImpl adminService;

    @Test
    @DisplayName("전체 회원수 조회")
    void count_members(){
        long count =10;

        when(memberRepository.countAllBy()).thenReturn(count);

        long l = adminService.searchAllMembers();
        assertThat(l).isEqualTo(count);
    }

    @Test
    @DisplayName("전체 요리글수 조회")
    void count_Recipes(){
        long count =1123123123;

        when(recipeRepository.countAllBy()).thenReturn(count);

        long l = adminService.searchAllRecipes();
        assertThat(l).isEqualTo(count);
    }

    @Test
    @DisplayName("전체 게시글 조회")
    void count_posts(){
        long count =550;

        when(postRepository.countAllBy()).thenReturn(count);

        long l = adminService.searchAllPosts();
        assertThat(l).isEqualTo(count);
    }
}