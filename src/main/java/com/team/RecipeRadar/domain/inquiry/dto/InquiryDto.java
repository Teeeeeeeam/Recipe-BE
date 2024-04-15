package com.team.RecipeRadar.domain.inquiry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryDto {

    private Long id;

    private MemberDto memberDto;

    private String inquiryTitle;    //문의사항 제목

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private InquiryDto inquiryDto;

    private LocalDateTime create_at;        //등록일

    private LocalDateTime updated_at;       //수정일
}
