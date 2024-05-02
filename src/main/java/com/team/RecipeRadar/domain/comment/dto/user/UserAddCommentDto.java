package com.team.RecipeRadar.domain.comment.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class UserAddCommentDto {

    @Schema(description = "댓글 내용", example = "댓글 작성!")
    @NotBlank(message = "댓글을 입력해주세요")
    private String commentContent;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "게시글 id", example = "1")
    private Long postId;

    @Schema(hidden = true)
    private LocalDateTime created_at;


    @JsonIgnore
    @JsonCreator
    public UserAddCommentDto(@JsonProperty("commentContent") String commentContent,
                             @JsonProperty("memberId") Long memberId,
                             @JsonProperty("postId") Long postId,
                             @JsonProperty("create_at")LocalDateTime created_at) {
        this.commentContent = commentContent;
        this.memberId = memberId;
        this.postId = postId;
        this.created_at = created_at;
    }


}
