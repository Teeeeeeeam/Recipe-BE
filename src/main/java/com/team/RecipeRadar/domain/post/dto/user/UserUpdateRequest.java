package com.team.RecipeRadar.domain.post.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserUpdatePostDto {

    @Schema(description = "요리글 id", example = "1")
    @NotEmpty(message = "요리글의 id를 넣어주세요")
    private Long postId;

    @Schema(description = "사용자 id", example = "1")
    @NotEmpty(message = "")
    private Long memberId;

    @Schema(description = "수정할 요리글 내용", example = "요리글 내용 수정!")
    @NotEmpty(message = "")
    private String postContent;

    @Schema(description = "수정할 요리글 제목", example = "요리글 제목 수정!")
    @NotEmpty(message = "")
    private String postTitle;


    @Schema(description = "요리 제공 인원", example = "2인분")
    @NotEmpty(message = "")
    private String postServing;

    @Schema(description = "요리 소요 시간", example = "15분")
    @NotEmpty(message = "")
    private String postCookingTime;

    @Schema(description = "요리 난이도", example = "상")
    @NotEmpty(message = "")
    private String postCookingLevel;

    @Schema(description = "게시글 이미지 URL", example = "http://example.com/newimage.jpg")
    @NotEmpty(message = "")
    private String postImageUrl;

}
