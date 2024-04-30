package com.team.RecipeRadar.domain.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.inquiry.domain.Inquiry;
import com.team.RecipeRadar.domain.like.domain.PostLike;
import com.team.RecipeRadar.domain.like.domain.RecipeLike;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
public class Member {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    Long id;

    String username;
    String nickName;
    String password;
    String loginId;
    String email;
    @JsonIgnore
    String roles;
    LocalDate join_date;
    String login_type;
    private boolean verified;

    @Builder.Default
    @OneToMany(mappedBy = "member",cascade = CascadeType.ALL)
    List<Inquiry> inquiries = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<Comment> comments = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<Post> posts = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<PostLike> postLikes = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
    List<RecipeLike> recipeLikes = new ArrayList<>();

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
