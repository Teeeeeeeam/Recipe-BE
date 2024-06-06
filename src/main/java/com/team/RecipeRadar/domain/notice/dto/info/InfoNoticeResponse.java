package com.team.RecipeRadar.domain.notice.dto.info;

import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InfoNoticeResponse {

    private boolean nextPage;
    private List<NoticeDto> notice;
}
