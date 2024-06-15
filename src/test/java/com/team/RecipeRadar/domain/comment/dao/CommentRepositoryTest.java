package com.team.RecipeRadar.domain.comment.dao;

import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
@DataJpaTest
@Import(QueryDslConfig.class)
@Transactional
@ActiveProfiles("test")
@Slf4j
class CommentRepositoryTest {

    @Autowired
    CommentRepository commentRepository;
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository articleRepository;
    @Autowired
    EntityManager entityManager;
    @Autowired PostRepository postRepository;

    @Test
    @DisplayName("댓글 삭제 테스트")
    void delete_comment() {
        Member member = Member.builder().username("test 유저").build();
        Member member1 = Member.builder().username("test 유저1").build();

        Post article = Post.builder().postContent("aaa").postServing("aaa").postCookingTime("aaa").postContent("asda").postCookingLevel("11").postLikeCount(0).postTitle("123").build();

        Comment comment = Comment.builder().commentContent("test 댓글 작성").member(member).post(article).build();
        Comment comment1 = Comment.builder().commentContent("test 댓글 작성1").member(member1).post(article).build();

        Member saveMember = memberRepository.save(member);
        memberRepository.save(member1);

        Post saveArticle = articleRepository.save(article);

        Comment saveComment = commentRepository.save(comment);
        Comment save = commentRepository.save(comment1);

        assertThat(saveMember).isNotNull();
        assertThat(saveArticle).isNotNull();
        assertThat(saveComment).isNotNull();

        // 댓글 삭제
        commentRepository.deleteMemberId(saveMember.getId(), saveComment.getId());

        entityManager.clear();
        // 댓글이 삭제되었는지 확인
        Optional<Comment> byId = commentRepository.findById(comment.getId());
        assertThat(byId).isEmpty();

        // 삭제되지 않은 댓글 확인
        Optional<Comment> byId1 = commentRepository.findById(save.getId());
        log.info("log={}",byId1);
        assertThat(byId1).isNotEmpty();
    }



    @Test
    @DisplayName("댓글 모두조회 페이징")
    void pageAll() {
        // 데이터베이스에 사용될 회원과 게시물 정보 생성
        Member member = Member.builder().build();
        memberRepository.save(member);

        Post article = Post.builder().postContent("aaa").postServing("aaa").postCookingTime("aaa").postContent("asda").postCookingLevel("11").postTitle("123").postLikeCount(0).build();
        articleRepository.save(article);

        // 첫 번째 게시물에 대한 댓글 10개 생성
        for (int i = 1; i <= 10; i++) {
            Comment comment = Comment.builder()
                    .member(member)
                    .commentContent("게시판 1번째 댓글")
                    .post(article)
                    .build();
            commentRepository.save(comment);
        }

        // 두 번째 게시물 생성
        Post article2 = Post.builder().postContent("aaa").postServing("aaa").postCookingTime("aaa").postContent("asda").postCookingLevel("11").postLikeCount(0).postTitle("123").build();
        articleRepository.save(article2);

        // 두 번째 게시물에 대한 댓글 5개 생성
        for (int i = 1; i <= 5; i++) {
            Comment comment = Comment.builder()
                    .member(member)
                    .commentContent("게시판 2번째 댓글")
                    .post(article2)
                    .build();
            commentRepository.save(comment);
        }

        // 첫 번째 페이지 조회
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> allByArticleId = commentRepository.findAllByPost_Id(article.getId(), pageable);
        assertThat(allByArticleId.getTotalPages()).isEqualTo(1);            // 총 페이지는 1개
        assertThat(allByArticleId.getTotalElements()).isEqualTo(10);        // 10개의 댓글
        assertThat(allByArticleId.getContent().size()).isEqualTo(10);       // 데이터도 10개

        // 두 번째 페이지 조회 (두 번째 게시물에 대한 댓글은 존재하지 않으므로 결과는 비어 있어야 함)
        Pageable pageable2 = PageRequest.of(0, 10);
        Page<Comment> allByArticleId2 = commentRepository.findAllByPost_Id(article2.getId(), pageable2);
        assertThat(allByArticleId2.getTotalPages()).isEqualTo(1);       // 페이지는 한개
        assertThat(allByArticleId2.getContent().size()).isEqualTo(5);   // 현재 페이지의 데이터 갯수는 5개
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void comment_update(){
        //when
        Comment comment = Comment.builder().commentContent("테스트 댓글 수정전").member(Member.builder().loginId("테스트아이디").build()).build();

        //given
        Comment save = commentRepository.save(comment);
        save.update("테스트 댓글 수정후!");

        //then
        assertThat(save.getMember().getLoginId()).isEqualTo(comment.getMember().getLoginId());
        assertThat(save.getCommentContent()).isEqualTo("테스트 댓글 수정후!");
        assertThat(save.getId()).isEqualTo(comment.getId());
        assertThat(save.getCommentContent()).isNotEqualTo("테스트 댓글 수정전");
    }
    
    @Test
    @DisplayName("게시글의 작상된 댓글 조회")
    void postsContainsComment(){

        Post post = Post.builder().postTitle("제목").build();
        Post save_post = postRepository.save(post);
        Member member = Member.builder().loginId("아이디").nickName("사용자1").build();
        Member member1 = Member.builder().loginId("아이디").nickName("사용자2").build();

        Member save = memberRepository.save(member);
        Member save2 = memberRepository.save(member1);

        List<Comment> commentList = List.of(
                Comment.builder().commentContent("댓글1").member(save).post(save_post).build(),
                Comment.builder().commentContent("댓글2").member(save2).post(save_post).build()
        );

        commentRepository.saveAll(commentList);

        PageRequest request = PageRequest.of(0, 3);
        Slice<CommentDto> postComment = commentRepository.getPostComment(post.getId(), null, request);

        List<CommentDto> content = postComment.getContent();
        assertThat(content).hasSize(2);
        assertThat(content.get(0).getCommentContent()).isEqualTo("댓글1");
        assertThat(content.get(0).getMember().getNickname()).isEqualTo("사용자1");
        assertThat(postComment.hasNext()).isFalse();

    }

}