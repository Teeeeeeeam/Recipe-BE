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

}