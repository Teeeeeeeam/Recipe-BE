package com.team.RecipeRadar.domain.notice.dto.admin;

import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NoticeDetailResponse {

    private NoticeDto notice;
}
