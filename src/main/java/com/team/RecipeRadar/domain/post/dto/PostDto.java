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

    private String postContent;

    private MemberDto memberDto;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PostDto postDto;

    private LocalDateTime create_at;        //등록일

    private LocalDateTime updated_at;       //수정일

    public void setLocalDateTime(){
        this.create_at = LocalDateTime.now().withSecond(0).withNano(0);
    }


}
