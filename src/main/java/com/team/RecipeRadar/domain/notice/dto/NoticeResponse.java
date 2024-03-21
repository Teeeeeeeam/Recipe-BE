package com.team.RecipeRadar.domain.notice.dto;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import lombok.Getter;

@Getter
public class NoticeResponse {

    private final String noticeTitle;
    private final String noticeContent;

    public  NoticeResponse(Notice notice) {
        this.noticeTitle = notice.getNoticeTitle();
        this.noticeContent = notice.getNoticeContent();
    }
}
