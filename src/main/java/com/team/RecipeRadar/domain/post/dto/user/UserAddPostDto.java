package com.team.RecipeRadar.domain.post.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
public class UserAddPostDto {

    @Schema(description = "요리글 제목", example = "요리제목!")
    private String postTitle;

    @Schema(description = "요리글 내용", example = "요리글 작성!")
    private String postContent;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "게시글 id", example = "1")
    private Long postId;

    @Schema(hidden = true)
    private LocalDateTime created_at;

    @JsonIgnore
    @JsonCreator
    public UserAddPostDto(@JsonProperty("postContent") String postContent,
                          @JsonProperty("postTitle") String postTitle,
                          @JsonProperty("memberId") Long memberId,
                          @JsonProperty("postId") Long postId,
                          @JsonProperty("created_at") LocalDateTime created_at) {
        this.postContent = postContent;
        this.postTitle = postTitle;
        this.memberId = memberId;
        this.postId = postId;
        this.created_at = created_at;
    }
}