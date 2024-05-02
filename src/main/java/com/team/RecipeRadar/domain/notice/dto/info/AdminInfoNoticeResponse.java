package com.team.RecipeRadar.domain.notice.dto.info;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AdminInfoNoticeResponse {

    private boolean nextpage;

    private List<AdminInfoNoticeRequest> content;
}
