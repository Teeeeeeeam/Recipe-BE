package com.team.RecipeRadar.domain.balckLIst.dto.response;

import com.team.RecipeRadar.domain.balckLIst.dto.BlackListDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BlackListResponse {

    private Boolean nextPage;
    private List<BlackListDto> blackList;
}
