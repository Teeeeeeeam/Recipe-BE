package com.team.RecipeRadar.domain.post.domain;

import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.utils.BaseTimeUtils;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(indexes = {
        @Index(columnList = "post_title"),
        @Index(columnList = "member_id"),
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "member")
public class Post extends BaseTimeUtils {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id")
    private Long id;

    @Column(name = "post_title")
    private String postTitle;

    @Column(name = "post_content",length = 1000)
    private String postContent;

    private String postPassword;

    @Column(name = "post_serving")
    private String postServing;

    @Column(name = "post_cooking_time")
    private String postCookingTime;

    @Column(name = "post_cooking_level")
    private String postCookingLevel;

    private Integer postLikeCount;

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
    }

    public static Post createPost(String postTitle,String postContent,String postServing,String postCookingTime,
                                  String postCookingLevel,Member member,Recipe recipe,String password){
        return Post.builder().postTitle(postTitle).postContent(postContent).postServing(postServing).postCookingTime(postCookingTime)
                .postCookingLevel(postCookingLevel).postLikeCount(0).member(member).recipe(recipe).postPassword(password).build();
    }
}
