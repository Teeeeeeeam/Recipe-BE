package com.team.RecipeRadar.domain.post.domain;

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
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", updatable = false)
    private Long id;

    @Column(name = "post_title", nullable = false)
    private String postTitle;

    @Column(name = "post_content", nullable = false)
    private String postContent;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    @Column(name = "post_serving", nullable = false)
    private String postServing;

    @Column(name = "post_cooking_time", nullable = false)
    private String postCookingTime;

    @Column(name = "post_cooking_level", nullable = false)
    private String postCookingLevel;

    @Column(name = "post_like_count", nullable = false)
    private Integer postLikeCount;

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

    public void update(String postTitle, String postContent) {
        this.postTitle = postTitle;
        this.postContent = postContent;
    }

    public void updateTime(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
