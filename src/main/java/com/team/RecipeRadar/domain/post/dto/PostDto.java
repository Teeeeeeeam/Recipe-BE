package com.team.RecipeRadar.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.post.domain.Post;
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

    private String nickName;        //요리글 작성자 정보

    private LocalDateTime create_at;        //등록일

    private LocalDateTime updated_at;       //수정일

    private String postServing;     // 요리 제공 인원

    private String postCookingTime;     // 요리 소요 시간

    private String postCookingLevel;        // 요리 난이도

    private Integer postLikeCount;      // 게시글 좋아요 수

    private String postImageUrl;        // 게시글 이미지 URL

    public PostDto(Long id, String postTitle,String img,String nickName) {
        this.id = id;
        this.postTitle = postTitle;
        this.postImageUrl = img;
        this.nickName = nickName;
    }

    public static PostDto of(Post post){
        return PostDto.builder()
                .id(post.getId())
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .nickName(post.getMember().getNickName())
                .create_at(post.getCreated_at())
                .postServing(post.getPostServing())
                .postCookingTime(post.getPostCookingTime())
                .postCookingLevel(post.getPostCookingLevel())
                .postLikeCount(post.getPostLikeCount())
                .postImageUrl(post.getPostImageUrl()).build();
    }
}
