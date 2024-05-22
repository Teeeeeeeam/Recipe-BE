package com.team.RecipeRadar.domain.post.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

@Data
@NoArgsConstructor
public class UserUpdateRequest {


    @Schema(description = "수정할 요리글 내용", example = "요리글 내용 수정!")
    @NotEmpty(message = "변경할 내용을 작성해주세요")
    private String postContent;

    @Schema(description = "수정할 요리글 제목", example = "요리글 제목 수정!")
    @NotEmpty(message = "변경할 제목을 입력해주세요")
    private String postTitle;

    @Schema(description = "요리 제공 인원", example = "2인분")
    @NotEmpty(message = "변경할 요리 인원을  선택해주세요.")
    private String postServing;

    @Schema(description = "요리 소요 시간", example = "15분")
    @NotEmpty(message = "변경할 요리 시간을 선택해주세요.")
    private String postCookingTime;

    @Schema(description = "요리 난이도", example = "상")
    @NotEmpty(message = "변경할 요리 난이도를 선택해주세요.")
    private String postCookingLevel;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Schema(description = "게시글 비밀번호", example = "123456")
    private String postPassword;

}
