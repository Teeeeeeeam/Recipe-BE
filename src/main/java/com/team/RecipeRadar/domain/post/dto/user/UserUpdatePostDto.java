package com.team.RecipeRadar.domain.post.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public class UserUpdatePostDto {
    @Schema(description = "수정할 요리글 내용", example = "요리글 수정!")
    private String postContent;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "요리글 id", example = "1")
    private Long postId;

    @Schema(hidden = true)
    private LocalDateTime update_At;

    @JsonIgnore
    @JsonCreator
    public UserUpdatePostDto(@JsonProperty String postContent,
                                @JsonProperty Long memberId,
                                @JsonProperty Long postId,
                                @JsonProperty LocalDateTime update_At) {
        this.postContent = postContent;
        this.memberId = memberId;
        this.postId = postId;
        this.update_At = update_At;
    }
}
