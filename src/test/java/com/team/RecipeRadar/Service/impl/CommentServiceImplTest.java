package com.team.RecipeRadar.Service.impl;

import com.team.RecipeRadar.Entity.Article;
import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.Entity.Member;
import com.team.RecipeRadar.dto.ArticleDto;
import com.team.RecipeRadar.dto.CommentDto;
import com.team.RecipeRadar.dto.MemberDto;
import com.team.RecipeRadar.repository.ArticleRepository;
import com.team.RecipeRadar.repository.CommentRepository;
import com.team.RecipeRadar.repository.MemberRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CommentServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock ArticleRepository articleRepository;
    @Mock CommentRepository commentRepository;

    @InjectMocks CommentServiceImpl commentService;


    @Test
    @DisplayName("Comment_save 저장테스트")
    public void testSave_ValidMemberAndArticle_ReturnsSavedComment() {
        // 범위
        MemberDto build = MemberDto.builder().id(1l).build();
        ArticleDto build1 = ArticleDto.builder().id(1l).build();

        LocalDateTime dateTime = LocalDateTime.of(2024,3,17,2,15);

        CommentDto commentDto = new CommentDto();
        commentDto.setComment_content("Test comment");
        commentDto.setMemberDto(build);
        commentDto.setArticleDto(build1);
        commentDto.setCreate_at(dateTime);

        Member member = new Member();
        member.setId(1L);
        Article article = new Article();
        article.setId(1L);

        // 목 리파지토리
        when(memberRepository.findById(1L)).thenReturn(Optional.of(member));
        when(articleRepository.findById(1L)).thenReturn(Optional.of(article));

        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // 실행
        Comment savedComment = commentService.save(commentDto);

        // 검증
        assertThat(savedComment).isNotNull();
        assertThat("Test comment").isEqualTo(savedComment.getComment_content());
        assertThat(savedComment.getUpdated_at()).isNull();
        assertThat(member).isEqualTo(savedComment.getMember());
        assertThat(article).isEqualTo(savedComment.getArticle());
    }

    @Test
    @DisplayName("NoSuchElementException 오류테스트")
    public void testSave_InvalidMemberOrArticle_ThrowsNoSuchElementException() {
        // 범위
        MemberDto build = MemberDto.builder().id(1l).build();
        ArticleDto build1 = ArticleDto.builder().id(1l).build();

        CommentDto commentDto = new CommentDto();
        commentDto.setComment_content("Test comment");
        commentDto.setMemberDto(build);
        commentDto.setArticleDto(build1);

        // 목 리파지토리
        when(memberRepository.findById(1L)).thenReturn(Optional.empty());
        when(articleRepository.findById(1L)).thenReturn(Optional.empty());

        // 검증 & 실행
        assertThatThrownBy(() -> commentService.save(commentDto))
                .isInstanceOf(NoSuchElementException.class);
    }
}