package com.team.RecipeRadar.domain.notice.domain;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.utils.BaseTimeUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
@Entity
@Table(indexes = {
        @Index(columnList = "notice_title")
})
@Getter
@Setter
@Builder
@ToString(exclude = "member")
@AllArgsConstructor
@NoArgsConstructor
public class Notice extends BaseTimeUtils {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id", updatable = false)
    private Long id;

    @Column(name = "notice_title", nullable = false)
    private String noticeTitle;

    @Column(name = "notice_content", nullable = false)
    private String noticeContent;


    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(hidden = true)
    @JoinColumn(name = "member_id")
    private Member member;
    public void update(String noticeTitle, String noticeContent) {
        this.noticeTitle = noticeTitle;
        this.noticeContent = noticeContent;
    }
    public static Notice createNotice(String noticeTitle, String noticeContent, Member member){
        return Notice.builder().noticeTitle(noticeTitle).noticeContent(noticeContent).member(member).build();
    }

}