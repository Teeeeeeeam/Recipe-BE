package com.team.RecipeRadar.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    private String comment_content;

    private MemberDto memberDto;


    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PostDto articleDto;

    private LocalDateTime create_at;        //등록일
    
    private LocalDateTime updated_at;       //수정일


    public void setLocalDateTime(){
        this.create_at = LocalDateTime.now().withSecond(0).withNano(0);
    }




}
