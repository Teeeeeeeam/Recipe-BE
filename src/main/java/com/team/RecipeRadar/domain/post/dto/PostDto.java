package com.team.RecipeRadar.domain.post.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PostDto {

    private Long id;

    private String postContent;     //요리글 내용

    private MemberDto memberDto;        //요리글 작성자 정보

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PostDto postDto;

    private LocalDateTime create_at;        //등록일

    private LocalDateTime updated_at;       //수정일

    private String postServing;     // 요리 제공 인원

    private String postCookingTime;     // 요리 소요 시간

    private String postCookingLevel;        // 요리 난이도

    private Integer postLikeCount;      // 게시글 좋아요 수

    private String postImageUrl;        // 게시글 이미지 URL

    public void setLocalDateTime(){
        this.create_at = LocalDateTime.now().withSecond(0).withNano(0);
    }


}
