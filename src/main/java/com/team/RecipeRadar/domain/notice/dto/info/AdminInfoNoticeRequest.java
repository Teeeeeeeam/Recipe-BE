package com.team.RecipeRadar.domain.notice.dto.info;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AdminInfoNoticeRequest {

    private Long id;

    private String noticeTitle;

    public static AdminInfoNoticeRequest of(Notice notice) {
        return new AdminInfoNoticeRequest(notice.getId(), notice.getNoticeTitle());
    }
}
