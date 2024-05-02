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
public class AdminAddNoticeDto {

    @Schema(description = "공지사항 제목", example = "공지사항 제목!")
    private String noticeTitle;

    @Schema(description = "사용자 id", example = "1")
    private  Long memberId;

    @Schema(description = "공지사항 id")
    private Long noticeId;

    @Schema(hidden = true)
    private LocalDateTime created_at;

    @JsonIgnore
    @JsonCreator
    public AdminAddNoticeDto(@JsonProperty("noticeTitle") String noticeTitle,
                             @JsonProperty("memberId") Long memberId) {
        this.noticeTitle = noticeTitle;
        this.memberId = memberId;
        this.noticeId = noticeId;
        this.created_at = created_at;
    }
}
