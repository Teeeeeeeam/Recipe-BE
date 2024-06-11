package com.team.RecipeRadar.domain.notice.dto.admin;

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
@Schema(name = "공지사항 수정 Request")
public class AdminUpdateRequest {

    @Schema(description = "수정할 공지사항 내용", example = "공지사항 내용 수정!")
    @NotEmpty(message = "변경할 내용을 작성해주세요")
    private String noticeContent;

    @Schema(description = "수정할 공지사항 제목", example = "공지사항 제목 수정!")
    @NotEmpty(message = "변경할 제목을 입력해주세요")
    private String noticeTitle;

}
