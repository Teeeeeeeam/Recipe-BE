package com.team.RecipeRadar.domain.notice.dto.info;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class AdminInfoDetailsResponse {

    private Long id;
    private String noticeTitle;
    private String noticeContent;
    private LocalDateTime create_At;
    private MemberDto member;

    public static AdminInfoDetailsResponse of(Long id,String title, String content,LocalDateTime create_At,MemberDto memberDto){
        return AdminInfoDetailsResponse.builder()
                .id(id)
                .noticeTitle(title)
                .noticeContent(content)
                .create_At(create_At)
                .member(memberDto).build();
    }

}
