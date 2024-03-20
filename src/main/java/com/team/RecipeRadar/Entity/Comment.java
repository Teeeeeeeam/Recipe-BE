package com.team.RecipeRadar.Entity;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", updatable = false)
    private Long id;

    @Column(name = "comment_content", nullable = false)
    private String commentContent;

    @Builder
    public Comment(String commentContent) {
        this.commentContent = commentContent;
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    public void update(String commentContent) {
        this.commentContent = commentContent;
    }
}
