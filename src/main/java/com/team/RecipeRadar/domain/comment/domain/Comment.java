package com.team.RecipeRadar.domain.comment.domain;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", updatable = false)
    private Long id;

    @Column(name = "comment_content", nullable = false)
    private String commentContent;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;


    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(hidden = true)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(hidden = true)
    @JoinColumn(name = "post_id")
    private Post post;

    @JsonIgnore
    public LocalDateTime getLocDateTime(){
        return this.created_at = LocalDateTime.now().withSecond(0).withNano(0);
    }
    @JsonIgnore
    public LocalDateTime getUpdate_LocDateTime(){
        return this.updated_at = LocalDateTime.now().withSecond(0).withNano(0);
    }

    public void updateTime(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }
}
