package com.team.RecipeRadar.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    private LocalDate createdAt;        //등록일

    private LocalDateTime updatedAt;       //수정일

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

    public static NoticeDto of(Notice notice,String nickName){
        MemberDto memberDto = MemberDto.builder().nickname(nickName).build();
        return NoticeDto.builder()
                .id(notice.getId())
                .createdAt(notice.getCreatedAt().toLocalDate())
                .noticeTitle(notice.getNoticeTitle())
                .member(memberDto).build();
    }

    public static NoticeDto detailsOf(Notice notice, String imgUrl){
        MemberDto memberDto = MemberDto.builder().id(notice.getMember().getId()).nickname(notice.getMember().getNickName()).build();
        return NoticeDto.builder()
                .id(notice.getId())
                .imgUrl(imgUrl)
                .createdAt(notice.getCreatedAt().toLocalDate())
                .noticeContent(notice.getNoticeContent())
                .noticeTitle(notice.getNoticeTitle())
                .member(memberDto).build();
    }

}
