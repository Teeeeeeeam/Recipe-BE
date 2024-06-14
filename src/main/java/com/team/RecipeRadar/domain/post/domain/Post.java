package com.team.RecipeRadar.domain.post.domain;

import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "member")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "post_title")
    private String postTitle;

    @Column(name = "post_content",length = 1000)
    private String postContent;

    private LocalDateTime created_at;

    private LocalDateTime updated_at;

    private String postPassword;

    @Column(name = "post_serving")
    private String postServing;

    @Column(name = "post_cooking_time")
    private String postCookingTime;

    @Column(name = "post_cooking_level")
    private String postCookingLevel;

    private Integer postLikeCount;

    @Column(name = "post_image_url")
    private String postImageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(hidden = true)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL,orphanRemoval = true)
    private List<UploadFile> uploadFiles = new ArrayList<>();

    public void update(String postTitle, String postContent, String postServing, String postCookingTime, String postCookingLevel,String postPassword) {
        this.postTitle = postTitle;
        this.postContent = postContent;
        this.postServing = postServing;
        this.postCookingTime = postCookingTime;
        this.postCookingLevel = postCookingLevel;
        this.postPassword= postPassword;
        this.updated_at= LocalDateTime.now().withNano(0).withSecond(0);
    }

    public void updateTime(LocalDateTime updated_at) {
        this.updated_at = updated_at;
    }

    static public PostDto of(Post post){

        return PostDto.builder()
                .id(post.getId())
                .postTitle(post.getPostTitle())
                .postContent(post.getPostContent())
                .postServing(post.getPostServing())
                .postCookingTime(post.getPostCookingTime())
                .postCookingLevel(post.getPostCookingLevel())
                .postImageUrl(post.getPostImageUrl())
                .postLikeCount(post.postLikeCount)
                .member(MemberDto.builder().nickname(post.getMember().getNickName()).build())
                .build();
    }
}
