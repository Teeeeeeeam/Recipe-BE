package com.team.RecipeRadar.domain.notice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UpdateNoticeRequest {

    private String noticeTitle;
    private String noticeContent;
}
