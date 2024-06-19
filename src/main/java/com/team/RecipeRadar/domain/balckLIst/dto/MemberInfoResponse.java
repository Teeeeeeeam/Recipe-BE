package com.team.RecipeRadar.domain.balckLIst.dto;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class MemberInfoResponse {

    private List<MemberDto> memberInfoes;
    private Boolean nextPage;

}
