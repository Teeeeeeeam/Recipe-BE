package com.team.RecipeRadar.repository;

import com.team.RecipeRadar.Entity.Article;
import com.team.RecipeRadar.Entity.Comment;
import com.team.RecipeRadar.Entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Slf4j
class CommentRepositoryTest {

    @Autowired CommentRepository commentRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired ArticleRepository articleRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    @Rollback
    @DisplayName("회원id와 댓글 id와 를 통해 데이터 삭제")
    void delete_CommentEntity(){
        Member member = Member.builder().username("test 유저").build();

        Member member1 = Member.builder().username("test 유저1").build();

        Article article = Article.builder().build();

        Comment comment = Comment.builder()
                .comment_content("test 댓글 작성")
                .member(member)
                .article(article)
                .build();

        Comment comment1 = Comment.builder()
                .id(2l)
                .comment_content("test 댓글 작성1")
                .member(member1)
                .article(article)
                .build();

        Member saveMember = memberRepository.save(member);
        Member saveMember1 = memberRepository.save(member1);


        Article saveArticle = articleRepository.save(article);
        Comment saveComment = commentRepository.save(comment);
        Comment saveComment1 = commentRepository.save(comment1);

        assertThat(saveMember).isNotNull();
        assertThat(saveArticle).isNotNull();
        assertThat(saveComment).isNotNull();

        commentRepository.deleteMemberId(member.getId(),comment.getId());

        //원래는 JPQL 을 사용하면 실행시에 플러쉬가 되어야하는데 왜 영속성 컨텍스트가 적용이안되는지 잘모르겟음... 그래서 강제로 flush 및 clear 을 사용하여 영속성 컨텍스트 초기화
        entityManager.flush();
        entityManager.clear();

        Optional<Comment> byId = commentRepository.findById(1l);
        assertThat(byId).isEmpty();
        Optional<Comment> byId1 = commentRepository.findById(2l);
        assertThat(byId1).isNotEmpty();
    }

    @Test
    @DisplayName("댓글 모두조회 페이징")
    void pageAll() {
        // 데이터베이스에 사용될 회원과 게시물 정보 생성
        Member member = Member.builder().build();
        memberRepository.save(member);

        Article article = Article.builder().build();
        articleRepository.save(article);

        // 첫 번째 게시물에 대한 댓글 10개 생성
        for (int i = 1; i <= 10; i++) {
            Comment comment = Comment.builder()
                    .member(member)
                    .comment_content("게시판 1번째 댓글")
                    .article(article)
                    .build();
            commentRepository.save(comment);
        }

        // 두 번째 게시물 생성
        Article article2 = Article.builder().build();
        articleRepository.save(article2);

        // 두 번째 게시물에 대한 댓글 5개 생성
        for (int i = 1; i <= 5; i++) {
            Comment comment = Comment.builder()
                    .member(member)
                    .comment_content("게시판 2번째 댓글")
                    .article(article2)
                    .build();
            commentRepository.save(comment);
        }

        // 첫 번째 페이지 조회
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> allByArticleId = commentRepository.findAllByArticle_Id(article.getId(), pageable);
        assertThat(allByArticleId.getTotalPages()).isEqualTo(1);            // 총 페이지는 1개
        assertThat(allByArticleId.getTotalElements()).isEqualTo(10);        // 10개의 댓글
        assertThat(allByArticleId.getContent().size()).isEqualTo(10);       // 데이터도 10개

        // 두 번째 페이지 조회 (두 번째 게시물에 대한 댓글은 존재하지 않으므로 결과는 비어 있어야 함)
        Pageable pageable2 = PageRequest.of(0, 10);
        Page<Comment> allByArticleId2 = commentRepository.findAllByArticle_Id(article2.getId(), pageable2);
        assertThat(allByArticleId2.getTotalPages()).isEqualTo(1);       // 페이지는 한개
        assertThat(allByArticleId2.getContent().size()).isEqualTo(5);   // 현재 페이지의 데이터 갯수는 5개
    }


}