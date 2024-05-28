package com.team.RecipeRadar.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NoticeDto {

    private Long id;

    private String noticeTitle;

    private String noticeContent;

    private LocalDateTime created_at;        //등록일

    private LocalDateTime updated_at;       //수정일

    private String imgUrl;
    private MemberDto member;


    private NoticeDto(Long id,String title,String img) {
        this.id = id;
        this.noticeTitle = title;
        this.imgUrl = img;
    }

    public static NoticeDto of(Long id, String noticeTitle, String imgUrl) {
        return new NoticeDto(id, noticeTitle, imgUrl);
    }

    public static NoticeDto of(Notice notice){

        return NoticeDto.builder()
                .id(notice.getId())
                .noticeTitle(notice.getNoticeTitle())
                .noticeContent(notice.getNoticeContent())
                .created_at(notice.getCreated_at()).build();
    }
}
