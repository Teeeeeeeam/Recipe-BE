package com.team.RecipeRadar.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(name = "게시글 DTO")
public class PostDto {

    private Long id;

    private String postTitle;

    private String postContent;     //요리글 내용

    private LocalDate createdAt;        //등록일

    private String postServing;     // 요리 제공 인원

    private String postCookingTime;     // 요리 소요 시간

    private String postCookingLevel;        // 요리 난이도

    private Integer postLikeCount;      // 게시글 좋아요 수

    private String postImageUrl;        // 게시글 이미지 URL

    private MemberDto member;

    private RecipeDto recipe;

    private List<CommentDto> comments;

    private PostDto(Long id, String loginId,String postTitle,String img,String nickName,String recipeTitle,Long recipeId,LocalDateTime creatAt) {
        this.id = id;
        this.member = new MemberDto();
        this.member.setLoginId(loginId);
        this.recipe = new RecipeDto();
        this.recipe.setId(recipeId);
        this.recipe.setTitle(recipeTitle);
        this.postTitle = postTitle;
        this.createdAt = creatAt.toLocalDate();
        this.postImageUrl = img;
        this.member.setNickname(nickName);
    }

    public static PostDto of(Long id, String loginId,String postTitle,String img,String nickName,String recipeTitle,Long recipeId,LocalDateTime create_at){
        return new PostDto(id,loginId,postTitle,img,nickName,recipeTitle,recipeId,create_at);
    }

    public static PostDto of(Post post,String img){
        MemberDto member = MemberDto.builder().nickname(post.getMember().getNickName()).build();
        return PostDto.builder().id(post.getId()).postTitle(post.getPostTitle()).postLikeCount(post.getPostLikeCount()).createdAt(post.getCreatedAt().toLocalDate())
                .postImageUrl(img).member(member).build();
    }
    public static PostDto of(Post post, String imgUrl, Recipe recipe,List<CommentDto> comments){
        return PostDto.builder()
                .id(post.getId())
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .createdAt(post.getCreatedAt().toLocalDate())
                .postServing(post.getPostServing())
                .postCookingTime(post.getPostCookingTime())
                .postCookingLevel(post.getPostCookingLevel())
                .postLikeCount(post.getPostLikeCount())
                .member(MemberDto.builder().nickname(post.getMember().getNickName()).build())
                .recipe(RecipeDto.builder().id(recipe.getId()).title(recipe.getTitle()).build())
                .comments(comments)
                .postImageUrl(imgUrl).build();
    }
}
