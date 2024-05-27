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

    private MemberDto member;

    private NoticeDto(Long id, String loginId, String noticeTitle, String noticeContent, LocalDateTime created_at) {
        this.id = id;
        this.member = new MemberDto();
        this.member.setLoginId(loginId);
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
        this.created_at = created_at;
    }

    private static NoticeDto of(Long id, String loginId, String noticeTitle, String noticeContent, LocalDateTime created_at) {
        return new NoticeDto(id, loginId, noticeTitle, noticeContent, created_at);
    }

    public static NoticeDto of(Notice notice){

        return NoticeDto.builder()
                .id(notice.getId())
                .noticeTitle(notice.getNoticeTitle())
                .noticeContent(notice.getNoticeContent())
                .created_at(notice.getCreated_at()).build();
    }
}
