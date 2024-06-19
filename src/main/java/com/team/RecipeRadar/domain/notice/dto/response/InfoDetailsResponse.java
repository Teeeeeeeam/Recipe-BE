package com.team.RecipeRadar.domain.notice.dto.response;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class InfoDetailsResponse {

    private Long id;
    private String noticeTitle;
    private String noticeContent;
    private LocalDate createdAt;
    private String imgUrl;
    private MemberDto member;

    public static InfoDetailsResponse of(NoticeDto noticeDto){
        return InfoDetailsResponse.builder()
                .id(noticeDto.getId())
                .noticeTitle(noticeDto.getNoticeTitle())
                .noticeContent(noticeDto.getNoticeContent())
                .createdAt(noticeDto.getCreatedAt())
                .member(noticeDto.getMember())
                .imgUrl(noticeDto.getImgUrl())
                .build();
    }

}
