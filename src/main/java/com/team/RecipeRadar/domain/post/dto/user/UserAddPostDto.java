package com.team.RecipeRadar.domain.post.dto.user;

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
@NoArgsConstructor
public class UserAddPostDto {

    @Schema(description = "요리글 제목", example = "요리제목!")
    private String postTitle;

    @Schema(description = "요리글 내용", example = "요리글 작성!")
    @NotBlank(message = "요리글을 입력해주세요")
    private String postContent;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "게시글 id", example = "1")
    private Long postId;

    @Schema(description = "요리 제공 인원", example = "4인분")
    private String postServing;

    @Schema(description = "요리 소요 시간", example = "30분")
    private String postCookingTime;

    @Schema(description = "요리 난이도", example = "상")
    private String postCookingLevel;

    @Schema(description = "게시글 이미지 URL", example = "http://example.com/image.jpg")
    private String postImageUrl;

    @Schema(hidden = true)
    private LocalDateTime created_at;

    @JsonIgnore
    @JsonCreator
    public UserAddPostDto(@JsonProperty("postTitle") String postTitle,
                          @JsonProperty("postContent") String postContent,
                          @JsonProperty("memberId") Long memberId,
                          @JsonProperty("postServing") String postServing,
                          @JsonProperty("postCookingTime") String postCookingTime,
                          @JsonProperty("postCookingLevel") String postCookingLevel,
                          @JsonProperty("postImageUrl") String postImageUrl) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.memberId = memberId;
        this.postId = postId;
        this.created_at = created_at;
        this.postServing = postServing;
        this.postCookingTime = postCookingTime;
        this.postCookingLevel = postCookingLevel;
        this.postImageUrl = postImageUrl;

    }
}