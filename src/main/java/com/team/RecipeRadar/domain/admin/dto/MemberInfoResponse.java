package com.team.RecipeRadar.domain.admin.dto;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MemberInfoResponse {

    private List<MemberDto> memberInfos;
    private Boolean nextPage;

}
