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

    @Column(name = "comment_title", nullable = false)
    private String commentTitle;

    @Column(name = "comment_content", nullable = false)
    private String commentContent;

    @Builder
    public Comment(String commentTitle, String commentContent) {
        this.commentTitle = commentTitle;
        this.commentContent = commentContent;
    }
}
