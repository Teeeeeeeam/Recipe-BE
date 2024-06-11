package com.team.RecipeRadar.domain.notice.dto.admin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@Schema(name = "공지사항 작성 Request")
public class AdminAddRequest {

    @Schema(description = "공지사항 제목", example = "공지사항 제목!")
    @NotEmpty(message = "공지사항 제목을 입력해주세요")
    private String noticeTitle;

    @Schema(description = "공지사항 내용", example = "공지사항 작성!")
    @NotBlank(message = "공지사항을 입력해주세요")
    private String noticeContent;

    @Schema(description = "사용자 id", example = "1")
    private  Long memberId;

}
