package com.team.RecipeRadar.Entity;

import lombok.*;

import javax.persistence.*;

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

    @Column(name = "post_serving", nullable = false)
    private String postServing;

    @Column(name = "post_cooking_time", nullable = false)
    private String postCookingTime;

    @Column(name = "post_cooking_level", nullable = false)
    private String postCookingLevel;

    @Builder
    public Post(String postTitle, String postContent, String postServing, String postCookingTime, String postCookingLevel) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postServing = postServing;
        this.postCookingTime = postCookingTime;
        this.postCookingLevel = postCookingLevel;
    }
    public void update(String postTitle, String postContent, String postServing, String postCookingTime, String postCookingLevel) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postServing = postServing;
        this.postCookingTime = postCookingTime;
        this.postCookingLevel = postCookingLevel;
    }
}
