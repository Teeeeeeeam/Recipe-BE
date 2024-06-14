package com.team.RecipeRadar.domain.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.recipe.domain.RecipeBookmark;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "posts")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Member {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    private String username;

    private String nickName;

    private String password;

    private String loginId;

    private String email;

    @JsonIgnore
    private String roles;

    private LocalDate createAt;

    private String login_type;

    private boolean verified;


    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    List<PostLike> postLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    List<RecipeLike> recipeLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL,orphanRemoval = true)
    List<RecipeBookmark> recipeBookmarks = new ArrayList<>();

    public List<String> getRoleList(){
        if(this.roles != null && this.roles.length() > 0){
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }
    public void update(String password){
        this.password = password;
    }

    public void updateNickName(String nickName){
        this.nickName = nickName;
    }

    public void updateEmail(String email){
        this.email = email;
    }
}
