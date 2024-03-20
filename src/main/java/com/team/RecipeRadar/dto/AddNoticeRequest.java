package com.team.RecipeRadar.dto;

import com.team.RecipeRadar.Entity.Notice;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddNoticeRequest {

    private String noticeTitle;
    private String noticeContent;

    public Notice toEntity() {
        return Notice.builder()
                .noticeTitle(noticeTitle)
                .noticeContent(noticeContent)
                .build();
    }
}