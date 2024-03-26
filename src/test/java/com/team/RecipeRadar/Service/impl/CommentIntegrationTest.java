package com.team.RecipeRadar.Service.impl;


import com.team.RecipeRadar.domain.comment.application.CommentServiceImpl;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;


//댓글 기능의 통합테스트
@SpringBootTest
@ActiveProfiles("test")
@Transactional
@Slf4j
public class CommentIntegrationTest {

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    CommentServiceImpl commentService;

    @Autowired
    PostRepository articleRepository;

    @Autowired
    CommentRepository commentRepository;

    @Test
    @Rollback(value = false)
    @DisplayName("댓글 등록 메서드 통합테스트")
    void test(){
        //when
        Member member = Member.builder().username("test").build();
        Post article = Post.builder().postContent("aaa").postServing("aaa").postCookingTime("aaa").postContent("asda").postCookingLevel("11").postTitle("123").build();
        Member member1 = Member.builder().username("test2").build();

        Member saveMember = memberRepository.save(member);
        Member saveMember1 = memberRepository.save(member1);

        Post saveArticle = articleRepository.save(article);

        MemberDto memberDto = MemberDto.builder().
                id(saveMember.getId()).
                username("test").build();

        MemberDto memberDto1 = MemberDto.builder().
                id(saveMember1.getId()).
                username("test1").build();

        PostDto articleDto = PostDto.builder()
                .id(saveArticle.getId())
                .build();

        CommentDto commentDto = CommentDto.builder()
                .comment_content("테스트 댓글")
                .memberDto(memberDto)
                .articleDto(articleDto).build();

        CommentDto commentDto1 = CommentDto.builder()
                .comment_content("테스트 댓글")
                .memberDto(memberDto1)
                .articleDto(articleDto).build();


        //실행
        Comment save = commentService.save(commentDto);
        Comment save1 = commentService.save(commentDto1);

        //검증
        assertThat(save.getMember().getUsername()).isEqualTo(member.getUsername());
        assertThat(save.getPost().getId()).isEqualTo(article.getId());
        assertThat(save.getCommentContent()).isEqualTo("테스트 댓글");
        
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0).withSecond(0);        //테스트시에만 가짜객체를 만들어서 하기떄문에 시간은 1분도 차이안남
        assertThat(save.getLocDateTime()).isEqualTo(localDateTime);
        assertThat(save1).isNotNull();
        assertThat(save1.getMember().getUsername()).isEqualTo(member1.getUsername());

    }
}
