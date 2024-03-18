package com.team.RecipeRadar.dto;

import com.team.RecipeRadar.Entity.Notice;
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
