package com.team.RecipeRadar.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import io.swagger.v3.oas.annotations.media.Schema;
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
public class CommentDto {
    
    private Long id;

    @Schema(description = "댓글 내용", example = "댓글 작성!")
    private String comment_content;

    @Schema(description = "작성자 닉네임", example = "나만의 냉장고")
    private String nickName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PostDto articleDto;

    private LocalDateTime create_at;        //등록일

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime updated_at;       //수정일


    public void setLocalDateTime(){
        this.create_at = LocalDateTime.now().withSecond(0).withNano(0);
    }


    public static CommentDto of(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .comment_content(comment.getCommentContent())
                .nickName(comment.getMember().getNickName())
                .create_at(comment.getCreated_at())
                .updated_at(comment.getUpdated_at()).build();
    }


}
