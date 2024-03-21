package com.team.RecipeRadar.domain.inquiry.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
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

    @Column(name = "inquiry_answer", nullable = false)
    private String inquiryAnswer;

    @Column(name = "inquiry_answered", nullable = false)
    private Boolean inquiryAnswered;

    @Builder
    public Inquiry(String inquiryTitle, String inquiryContent, String inquiryAnswer, Boolean inquiryAnswered) {
        this.inquiryTitle = inquiryTitle;
        this.inquiryContent = inquiryContent;
        this.inquiryAnswer = inquiryAnswer;
        this.inquiryAnswered = inquiryAnswered;
    }

    public void update(String inquiryTitle, String inquiryContent) {
        this.inquiryTitle = inquiryTitle;
        this.inquiryContent = inquiryContent;
    }

    public void setInquiryAnswer(String inquiryAnswer) {
        this.inquiryAnswer = inquiryAnswer;
    }

    public void setInquiryAnswered(boolean inquiryAnswered) {
        this.inquiryAnswered = inquiryAnswered;
    }
}
