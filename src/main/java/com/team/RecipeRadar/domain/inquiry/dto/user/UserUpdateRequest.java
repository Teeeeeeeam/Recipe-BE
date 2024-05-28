package com.team.RecipeRadar.domain.inquiry.dto.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserUpdateRequest {

    @Schema(description = "수정할 문의사항 내용", example = "문의사항 내용 수정!")
    @NotEmpty(message = "변경할 내용을 작성해주세요")
    private String inquiryContent;

    @Schema(description = "수정할 문의사항 제목", example = "문의사항 제목 수정!")
    @NotEmpty(message = "변경할 제목을 작성해주세요")
    private String inquiryTitle;

    @NotEmpty(message = "비밀번호를 입력해주세요")
    @Schema(description = "문의사항 비밀번호", example = "123456")
    private String inquiryPassword;


}
