package com.team.RecipeRadar.global.jwt.dto;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.Data;

@Data
public class MemberInfoResponse {

    private Long id;
    private String loginId;
    private String nickName;
    private String loginType;
    private String roles;

    private MemberInfoResponse(Long id, String loginId, String nickName, String loginType,String roles) {
        this.id = id;
        this.loginId = loginId;
        this.nickName = nickName;
        this.loginType = loginType;
        this.roles = roles;
    }

    public static MemberInfoResponse of(MemberDto memberDto){
        return new MemberInfoResponse(memberDto.getId(), memberDto.getLoginId(), memberDto.getNickname(), memberDto.getLogin_type(),memberDto.getRoles());
    }
}
