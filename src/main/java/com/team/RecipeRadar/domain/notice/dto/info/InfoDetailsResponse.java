package com.team.RecipeRadar.domain.notice.dto.info;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notice.dto.NoticeDto;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class InfoDetailsResponse {

    private Long id;
    private String noticeTitle;
    private String noticeContent;
    private LocalDateTime createdAt;
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
