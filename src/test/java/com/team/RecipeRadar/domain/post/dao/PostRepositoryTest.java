package com.team.RecipeRadar.domain.post.dao;

import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.post.dto.user.PostDetailResponse;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.Image.dao.ImgRepository;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfig.class)
@Transactional
@ActiveProfiles("test")
@Slf4j
class PostRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;
    @Autowired ImgRepository imgRepository;
    @Autowired RecipeRepository recipeRepository;


    @Test
    @DisplayName("무한 페이징 테스트 진행")
    void pagingTest(){
        Pageable request = PageRequest.of(0, 2);

        List<Post> postList = new ArrayList<>();
        Member member = Member.builder().nickName("닉네임 1").build();
        Member member1 = Member.builder().nickName("닉네임 2").build();
        Member save = memberRepository.save(member);
        Member save1 = memberRepository.save(member1);

        Recipe recipe = Recipe.builder().id(1l).title("testTitle").build();
        Recipe saveRecipe = recipeRepository.save(recipe);

        postList.add(Post.builder().postTitle("tit").postContent("컨텐트1").recipe(saveRecipe).postCookingLevel("coo").member(save).postCookingTime("ti").build());
        postList.add(Post.builder().postTitle("tit").postContent("컨텐트2").recipe(saveRecipe).postCookingLevel("coo").member(save1).postCookingTime("ti").build());
        postList.add(Post.builder().postTitle("tit").postContent("컨텐트3").recipe(saveRecipe).postCookingLevel("coo").member(save).postCookingTime("ti").build());
        postList.add(Post.builder().postTitle("tit").postContent("컨텐트4").recipe(saveRecipe).postCookingLevel("coo").member(save).postCookingTime("ti").build());

        List<Post> posts = postRepository.saveAll(postList);

        UploadFile uploadFile = UploadFile.builder().storeFileName("tesNmae").originFileName("originName").post(posts.get(0)).recipe(saveRecipe).build();
        UploadFile uploadFile1 = UploadFile.builder().storeFileName("tesNmae").originFileName("originName").post(posts.get(1)).recipe(saveRecipe).build();
        UploadFile uploadFile2 = UploadFile.builder().storeFileName("tesNmae").originFileName("originName").post(posts.get(2)).recipe(saveRecipe).build();
        UploadFile uploadFile3 = UploadFile.builder().storeFileName("tesNmae").originFileName("originName").post(posts.get(3)).recipe(saveRecipe).build();
        imgRepository.save(uploadFile);
        imgRepository.save(uploadFile1);
        imgRepository.save(uploadFile2);
        imgRepository.save(uploadFile3);

        Slice<PostDto> allPost = postRepository.getAllPost(request);

        assertThat(allPost.hasNext()).isTrue();
        assertThat(allPost.getContent()).hasSize(2);
    }

    @Test
    @DisplayName("게시글 상세 조회 테스트")
    void details_Posts(){
        Member member = Member.builder().nickName("닉네임 1").build();
        Member save = memberRepository.save(member);
        Recipe recipe = Recipe.builder().id(1l).title("testTitle").build();
        Recipe saveRecipe = recipeRepository.save(recipe);
        Post post = Post.builder().postCookingTime("시간").postServing("인원수").postTitle("제목").postCookingLevel("난이도").member(save).recipe(saveRecipe).build();

        Comment comment = Comment.builder().post(post).commentContent("댓글 ㅈ가성~").member(member).build();
        Comment save1 = commentRepository.save(comment);

        UploadFile uploadFile = UploadFile.builder().storeFileName("tesNmae").originFileName("originName").post(post).recipe(saveRecipe).build();
        imgRepository.save(uploadFile);

        postRepository.save(post);
        PostDetailResponse postDetailResponse = postRepository.postDetails(post.getId());
        assertThat(postDetailResponse).isNotNull();
        assertThat(postDetailResponse.getPost().getPostTitle()).isEqualTo(post.getPostTitle());
        assertThat(postDetailResponse.getComments().get(0).getComment_content()).isEqualTo(save1.getCommentContent());
    }
}