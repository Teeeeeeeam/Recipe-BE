package com.team.RecipeRadar.domain.comment.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.domain.Member;
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
@Schema(name = "댓글 DTO")
public class CommentDto {
    
    private Long id;

    @Schema(description = "댓글 내용", example = "댓글 작성!")
    private String commentContent;

    @Schema(description = "작성자 닉네임", example = "나만의 냉장고")
    private String nickName;

    private LocalDateTime createdAt;

    private MemberDto member;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private PostDto articleDto;

    public static CommentDto of(Comment comment){
        return CommentDto.builder()
                .id(comment.getId())
                .commentContent(comment.getCommentContent())
                .nickName(comment.getMember().getNickName())
                .createdAt(truncateNanos(comment))
                .build();
    }

    public static CommentDto admin(Comment comment){
        Member member = comment.getMember();
        MemberDto memberDto = MemberDto.builder().loginId(member.getLoginId()).nickname(member.getNickName()).username(member.getUsername()).build();
        return CommentDto.builder()
                .id(comment.getId())
                .commentContent(comment.getCommentContent())
                .createdAt(truncateNanos(comment))
                .member(memberDto).build();
    }

    private static LocalDateTime truncateNanos(Comment comment) {
        return comment.getCreatedAt().withNano(0);
    }

}
