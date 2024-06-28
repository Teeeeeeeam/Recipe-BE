package com.team.RecipeRadar.domain.comment.dao;

import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
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

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class CommentRepositoryTest {

    @Autowired CommentRepository commentRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository articleRepository;
    @Autowired EntityManager entityManager;
    @Autowired PostRepository postRepository;
    @Autowired RecipeRepository recipeRepository;

    private List<Member> members;
    private List<Post> posts;
    private List<Comment> comments;
    private Recipe recipe;

    @BeforeEach
    void setUp() {
        members = List.of(
                Member.builder().username("test 유저").build(),
                Member.builder().username("test 유저1").build()
        );

        recipe = Recipe.builder().title("레시피").build();
        recipeRepository.save(recipe);
        posts = List.of(
                Post.builder().postContent("aaa").postServing("aaa").postCookingTime("aaa").postContent("asda").postCookingLevel("11").recipe(recipe).postLikeCount(0).postTitle("123").build()
        );

        memberRepository.saveAll(members);
        postRepository.saveAll(posts);

        comments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            comments.add(Comment.builder()
                    .member(members.get(0))
                    .commentContent("게시판 1번째 댓글")
                    .post(posts.get(0))
                    .build());
        }
        commentRepository.saveAll(comments);
    }

    @Test
    @DisplayName("댓글 삭제 테스트")
    void delete_comment() {
        // 댓글 삭제
        commentRepository.deleteByMemberIdAndCommentId(members.get(0).getId(), comments.get(0).getId());

        //영속성 컨테이너 초기화
        entityManager.clear();
        // 첫번쨰 댓글을 삭제
        Optional<Comment> byId = commentRepository.findById(comments.get(0).getId());
        assertThat(byId).isEmpty();

       // 두번째 댓글은 삭제되지 않음
        Optional<Comment> byId1 = commentRepository.findById(comments.get(1).getId());
        assertThat(byId1).isNotEmpty();
    }



    @Test
    @DisplayName("댓글 모두조회 페이징")
    void pageAll() {
        // 첫 번째 페이지 조회
        Pageable pageable = PageRequest.of(0, 10);
        Page<Comment> allByArticleId = commentRepository.findAllByPostId(posts.get(0).getId(), pageable);
        assertThat(allByArticleId.getTotalPages()).isEqualTo(1);            // 총 페이지는 1개
        assertThat(allByArticleId.getTotalElements()).isEqualTo(10);        // 10개의 댓글
        assertThat(allByArticleId.getContent().size()).isEqualTo(10);       // 데이터도 10개
    }

    @Test
    @DisplayName("댓글 수정 테스트")
    void comment_update(){

        Comment comment = comments.get(0);
        comment.update("테스트 댓글 수정후!");

        assertThat(comment.getCommentContent()).isEqualTo("테스트 댓글 수정후!");
        assertThat(comment.getCommentContent()).isNotEqualTo("게시판 1번째 댓글");
    }
    
    @Test
    @DisplayName("게시글의 작상된 댓글 조회")
    void postsContainsComment(){
        PageRequest request = PageRequest.of(0, 3);
        Slice<CommentDto> postComment = commentRepository.getCommentsByPostId(posts.get(0).getId(), null, request);

        List<CommentDto> content = postComment.getContent();
        assertThat(content).hasSize(3);
        assertThat(content.get(0).getCommentContent()).isEqualTo("게시판 1번째 댓글");
        assertThat(content.get(0).getMember().getUsername()).isEqualTo("test 유저");
        assertThat(postComment.hasNext()).isTrue();

    }

    @Test
    @DisplayName("레시피 id를 통해 게시글의 달린 댓글 삭제 테스트")
    void deletePostCommentByRecipeId(){
        commentRepository.deleteCommentsByRecipeId(recipe.getId());
        List<Comment> allByPostId = commentRepository.findAllByPostId(posts.get(0).getId());
        assertThat(allByPostId).isEmpty();
    }
    
    @Test
    @DisplayName("postId를 통해 댓글 삭제 테스트")
    void deleteCommentByPostId(){
        commentRepository.deleteByPostId(posts.get(0).getId());
        List<Comment> allByPostId = commentRepository.findAllByPostId(posts.get(0).getId());
        assertThat(allByPostId).isEmpty();
    }
    
    @Test
    @DisplayName("postId를 통해 작성된 댓글 모두조회 페이징 테스트")
    void findAllPostIdPage(){
        PageRequest request = PageRequest.of(0, 10);
        Page<Comment> allByPostId = commentRepository.findAllByPostId(posts.get(0).getId(), request);
        assertThat(allByPostId).hasSize(10); //총 10개 조회
        assertThat(allByPostId.getContent().get(0).getCommentContent()).isEqualTo("게시판 1번째 댓글");
    }
}