package com.team.RecipeRadar.domain.notice.dto.admin;

import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import lombok.Getter;

import java.util.List;

@Getter
public class NoticeResponse {

    private boolean nextpage;
    private List<NoticeDto> notices;

    public  NoticeResponse(boolean nextpage, List<NoticeDto> notices) {
        this.nextpage = nextpage;
        this.notices = notices;

    }
}
