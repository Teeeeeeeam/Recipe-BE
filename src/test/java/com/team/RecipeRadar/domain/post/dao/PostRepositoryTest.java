package com.team.RecipeRadar.domain.post.dao;

import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@EnableJpaAuditing
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class PostRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired ImgRepository imgRepository;
    @Autowired RecipeRepository recipeRepository;

    Member member;
    Member member1;
    Recipe saveRecipe;
    List<Post> posts;

    @BeforeEach
    void setUp() {
        member = Member.builder().loginId("testId").nickName("닉네임 1").build();
        member1 = Member.builder().nickName("닉네임 2").build();
        memberRepository.save(member);
        memberRepository.save(member1);

        Recipe recipe = Recipe.builder().id(1L).title("testTitle").build();
        saveRecipe = recipeRepository.save(recipe);

        List<Post> postList = new ArrayList<>();
        postList.add(Post.builder().postTitle("searchPost").postContent("컨텐트1").recipe(saveRecipe).postCookingLevel("coo").postLikeCount(120).member(member).postCookingTime("ti").build());
        postList.add(Post.builder().postTitle("tit1").postContent("컨텐트2").recipe(saveRecipe).postCookingLevel("coo").postLikeCount(1000).member(member1).postCookingTime("ti").build());
        postList.add(Post.builder().postTitle("tit2").postContent("컨텐트3").recipe(saveRecipe).postCookingLevel("coo").postLikeCount(150).member(member).postCookingTime("ti").build());
        postList.add(Post.builder().postTitle("tit3").postContent("컨텐트4").recipe(saveRecipe).postCookingLevel("coo").postLikeCount(0).member(member).postCookingTime("ti").build());

        posts = postRepository.saveAll(postList);

        imgRepository.save(UploadFile.builder().storeFileName("tesNmae").originFileName("originName").post(posts.get(0)).recipe(saveRecipe).build());
        imgRepository.save(UploadFile.builder().storeFileName("tesNmae").originFileName("originName").post(posts.get(1)).recipe(saveRecipe).build());
        imgRepository.save(UploadFile.builder().storeFileName("tesNmae").originFileName("originName").post(posts.get(2)).recipe(saveRecipe).build());
        imgRepository.save(UploadFile.builder().storeFileName("tesNmae").originFileName("originName").post(posts.get(3)).recipe(saveRecipe).build());
    }

    @Test
    @DisplayName("무한 페이징 테스트 진행")
    void pagingTest() {
        Pageable request = PageRequest.of(0, 2);
        Slice<PostDto> allPost = postRepository.getAllPost(null, request);

        assertThat(allPost.hasNext()).isTrue();
        assertThat(allPost.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("게시글 상세 조회 테스트")
    void details_Posts() {
        Comment comment = Comment.builder().post(posts.get(0)).commentContent("댓글 ㅈ가성~").member(member).build();
        commentRepository.save(comment);

        PostDto postDto = postRepository.postDetails(posts.get(0).getId());

        assertThat(postDto).isNotNull();
        assertThat(postDto.getPostTitle()).isEqualTo(posts.get(0).getPostTitle());
        assertThat(postDto.getComments().get(0).getCommentContent()).isEqualTo(comment.getCommentContent());
    }

    @Test
    @DisplayName("게시글 검색 페이징 테스트 진행")
    void post_search() {
        Pageable request = PageRequest.of(0, 2);
        String loginId = "testId";

        Slice<PostDto> search_loginId = postRepository.searchPosts(loginId, null, null, null, request);
        Slice<PostDto> search_loginId_and_title_postTitle = postRepository.searchPosts(null, null, "searchPost", null, request);

        assertThat(search_loginId.hasNext()).isTrue();
        assertThat(search_loginId.getContent()).hasSize(2);

        assertThat(search_loginId_and_title_postTitle.hasNext()).isFalse();
        assertThat(search_loginId_and_title_postTitle.getContent()).hasSize(1);
    }

    @Test
    @DisplayName("레시피 페이지에서 좋아요순 4개 출력")
    void getRecipeTopByLikes(){
        List<PostDto> topRecipesByLikes = postRepository.getTopRecipesByLikes(saveRecipe.getId());

        assertThat(topRecipesByLikes.get(0).getPostTitle()).isEqualTo("tit1");
        assertThat(topRecipesByLikes.get(1).getPostTitle()).isEqualTo("tit2");
        assertThat(topRecipesByLikes.get(2).getPostTitle()).isEqualTo("searchPost");
        assertThat(topRecipesByLikes.get(3).getPostTitle()).isEqualTo("tit3");
    }

}
