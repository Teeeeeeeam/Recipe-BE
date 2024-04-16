package com.team.RecipeRadar.domain.notice.domain;

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
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id", updatable = false)
    private Long id;

    @Column(name = "notice_title", nullable = false)
    private String noticeTitle;

    @Column(name = "notice_content", nullable = false)
    private String noticeContent;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

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

    public void update(String noticeTitle) {
        this.noticeTitle = noticeTitle;
    }

    public void updateTime(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}