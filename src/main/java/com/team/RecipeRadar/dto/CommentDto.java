package com.team.RecipeRadar.dto;

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

    private ArticleDto articleDto;

    private LocalDateTime create_at;        //등록일
    
    private LocalDateTime updated_at;       //수정일


    public void setLocalDateTime(){
        this.create_at = LocalDateTime.now().withSecond(0).withNano(0);
    }



}
