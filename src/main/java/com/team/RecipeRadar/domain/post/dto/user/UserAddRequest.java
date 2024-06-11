package com.team.RecipeRadar.domain.post.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
@Schema(name = "게시글 작성 Request")
public class UserAddRequest {

    @Schema(description = "요리글 제목", example = "요리제목!")
    @NotEmpty(message = "요리 제목을 입력해주세요")
    private String postTitle;

    @Schema(description = "요리글 내용", example = "요리글 작성!")
    @NotBlank(message = "요리글을 입력해주세요")
    private String postContent;
    
    @Schema(description = "요리 소요 시간", example = "30분")
    @NotEmpty(message = "요리 시간을 선택하세요")
    private String postCookingTime;

    @Schema(description = "요리 난이도", example = "상")
    @NotEmpty(message = "요리 난이도를 선택해주세요")
    private String postCookingLevel;

    @Schema(description = "요리 제공 인원", example = "4인분")
    @NotEmpty(message = "인원수를 선택해 주세요")
    private String postServing;

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "레시피 id", example = "1")
    private Long recipe_id;
    
    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Schema(description = "게시글 비밀번호", example = "123456")
    private String postPassword;


}