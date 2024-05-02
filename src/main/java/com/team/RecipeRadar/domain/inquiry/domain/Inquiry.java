package com.team.RecipeRadar.domain.inquiry.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.RecipeRadar.domain.member.domain.Member;
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

    @Column(name = "inquiry_answer", nullable = false)
    private String inquiryAnswer;

    @Column(name = "inquiry_answered", nullable = false)
    private Boolean inquiryAnswered;

    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(hidden = true)
    @JoinColumn(name = "member_id")
    private Member member;

    @JsonIgnore
    public LocalDateTime getLocDateTime(){
        return this.created_at = LocalDateTime.now().withSecond(0).withNano(0);
    }
    @JsonIgnore
    public LocalDateTime getUpdate_LocDateTime(){
        return this.updated_at = LocalDateTime.now().withSecond(0).withNano(0);
    }

    public void update(String inquiryTitle) {
        this.inquiryTitle = inquiryTitle;
    }

    public void updateTime(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
