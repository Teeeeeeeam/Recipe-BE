package com.team.RecipeRadar.domain.notice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminDeleteRequest {

    @Schema(description = "사용자 id", example = "1")
    private Long memberId;

    @Schema(description = "공지사항 id", example = "1")
    private Long noticeId;
}
