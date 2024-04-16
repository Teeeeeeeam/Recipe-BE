package com.team.RecipeRadar.domain.notice.dto.admin;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AdminUpdateNoticeDto {

    @Schema(description = "공지사항 제목", example = "문의사항 제목 수정!")
    private String noticeTitle;

    @Schema(description = "사용자 id", example = "1")
    private  Long memberId;

    @Schema(description = "공지사항 id")
    private Long noticeId;

    @Schema(hidden = true)
    private LocalDateTime update_at;

    @JsonIgnore
    @JsonCreator
    public AdminUpdateNoticeDto(@JsonProperty("noticeTitle") String noticeTitle,
                             @JsonProperty("memberId") Long memberId,
                             @JsonProperty("noticeId") Long noticeId) {
        this.noticeTitle = noticeTitle;
        this.memberId = memberId;
        this.noticeId = noticeId;
    }
}
