package com.team.RecipeRadar.domain.inquiry.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.RecipeRadar.domain.inquiry.dto.InquiryDto;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "inquiry_id", updatable = false)
    private Long id;

    @Column(name = "inquiry_title", nullable = false)
    private String inquiryTitle;

    @Column(name = "inquiry_content", nullable = false)
    private String inquiryContent;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    private String inquiryPassword;

    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(hidden = true)
    @JoinColumn(name = "member_id")
    private Member member;


    public void update(String inquiryTitle, String inquiryContent, String inquiryPassword) {
        this.inquiryTitle = inquiryTitle;
        this.inquiryContent = inquiryContent;
        this.inquiryPassword = inquiryPassword;
        this.updated_at= LocalDateTime.now().withNano(0).withSecond(0);
    }

    public void updateTime(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    static public InquiryDto of(Inquiry inquiry) {

        return InquiryDto.builder()
                .id(inquiry.getId())
                .inquiryTitle(inquiry.getInquiryTitle())
                .inquiryContent(inquiry.getInquiryContent())
                .member(MemberDto.builder().nickname(inquiry.getMember().getNickName()).build())
                .build();
    }
}
