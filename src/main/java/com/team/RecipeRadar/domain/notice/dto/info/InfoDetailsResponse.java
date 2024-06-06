package com.team.RecipeRadar.domain.notice.dto.info;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InfoDetailsResponse {

    private Long id;
    private String noticeTitle;
    private String noticeContent;
    private LocalDateTime created_at;
    private MemberDto member;

    public static InfoDetailsResponse of(Long id, String title, String content, LocalDateTime create_at, MemberDto memberDto){
        return InfoDetailsResponse.builder()
                .id(id)
                .noticeTitle(title)
                .noticeContent(content)
                .created_at(create_at)
                .member(memberDto).build();
    }

}
