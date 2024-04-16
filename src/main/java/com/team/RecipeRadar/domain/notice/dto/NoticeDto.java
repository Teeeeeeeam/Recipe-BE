package com.team.RecipeRadar.domain.notice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoticeDto {

    private Long id;

    private MemberDto memberDto;

    private String inquiryTitle;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private NoticeDto noticeDto;

    private LocalDateTime create_at;        //등록일

    private LocalDateTime updated_at;       //수정일
}
