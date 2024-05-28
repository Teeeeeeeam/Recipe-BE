package com.team.RecipeRadar.domain.inquiry.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
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

    private String inquiryContent;

    private LocalDateTime created_at;        //등록일

    private LocalDateTime updated_at;       //수정일

    private MemberDto member;

    private InquiryDto(Long id, String loginId, String inquiryTitle, String inquiryContent, String nickName,LocalDateTime created_at) {
        this.id = id;
        this.member = new MemberDto();
        this.member.setLoginId(loginId);
        this.inquiryTitle = inquiryTitle;
        this.inquiryContent = inquiryContent;
        this.created_at=created_at;
        this.member.setNickname(nickName);
    }

    public static InquiryDto of(Long id, String loginId, String inquiryTitle, String inquiryContent, String nickName, LocalDateTime created_at) {
        return new InquiryDto(id, loginId, inquiryTitle, inquiryContent, nickName, created_at);
    }

    public static InquiryDto of(Inquiry inquiry) {
        MemberDto memberDto = new MemberDto();
        memberDto.setNickname(inquiry.getMember().getNickName());

        return InquiryDto.builder()
                .id(inquiry.getId())
                .inquiryTitle(inquiry.getInquiryTitle())
                .inquiryContent(inquiry.getInquiryContent())
                .member(memberDto)
                .created_at(inquiry.getCreated_at())
                .build();
    }

}
