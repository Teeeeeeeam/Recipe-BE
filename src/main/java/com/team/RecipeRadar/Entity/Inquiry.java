package com.team.RecipeRadar.Entity;

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

    @Builder
    public Inquiry(String inquiryTitle, String inquiryContent) {
        this.inquiryTitle = inquiryTitle;
        this.inquiryContent = inquiryContent;
    }

    public void update(String inquiryTitle, String inquiryContent) {
        this.inquiryTitle = inquiryTitle;
        this.inquiryContent = inquiryContent;
    }
}
