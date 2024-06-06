package com.team.RecipeRadar.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.recipe.dto.RecipeDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PostDto {

    private Long id;

    private String postTitle;

    private String postContent;     //요리글 내용

    private LocalDateTime create_at;        //등록일

    private LocalDateTime updated_at;       //수정일

    private String postServing;     // 요리 제공 인원

    private String postCookingTime;     // 요리 소요 시간

    private String postCookingLevel;        // 요리 난이도

    private Integer postLikeCount;      // 게시글 좋아요 수

    private String postImageUrl;        // 게시글 이미지 URL

    private MemberDto member;

    private RecipeDto recipe;

    private PostDto(Long id, String loginId,String postTitle,String img,String nickName,String recipeTitle,Long recipeId,LocalDateTime create_at) {
        this.id = id;
        this.member = new MemberDto();
        this.member.setLoginId(loginId);
        this.recipe = new RecipeDto();
        this.recipe.setId(recipeId);
        this.recipe.setTitle(recipeTitle);
        this.postTitle = postTitle;
        this.create_at=create_at;
        this.postImageUrl = img;
        this.member.setNickname(nickName);
    }

    public static PostDto of(Long id, String loginId,String postTitle,String img,String nickName,String recipeTitle,Long recipeId,LocalDateTime create_at){
        return new PostDto(id,loginId,postTitle,img,nickName,recipeTitle,recipeId,create_at);
    }
    public static PostDto of(Post post, String imgUrl, Recipe recipe){
        MemberDto memberDto = new MemberDto();
        memberDto.setNickname(post.getMember().getNickName());

        RecipeDto recipeDto = new RecipeDto();
        recipeDto.setId(recipe.getId());
        recipeDto.setTitle(recipe.getTitle());
        return PostDto.builder()
                .id(post.getId())
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .member(memberDto)
                .recipe(recipeDto)
                .create_at(post.getCreated_at())
                .postServing(post.getPostServing())
                .postCookingTime(post.getPostCookingTime())
                .postCookingLevel(post.getPostCookingLevel())
                .postLikeCount(post.getPostLikeCount())
                .postImageUrl(imgUrl).build();
    }
}
