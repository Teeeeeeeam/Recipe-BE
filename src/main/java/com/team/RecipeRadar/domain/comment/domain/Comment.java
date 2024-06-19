package com.team.RecipeRadar.domain.comment.domain;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.utils.BaseTimeUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.persistence.*;

@Entity
@Builder
@Getter
@AllArgsConstructor
@ToString(exclude = {"member", "post"})
@NoArgsConstructor
public class Comment extends BaseTimeUtils {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id", updatable = false)
    private Long id;

    @Column(name = "comment_content", nullable = false)
    private String commentContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(hidden = true)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @Schema(hidden = true)
    @JoinColumn(name = "post_id")
    private Post post;

    public void update(String commentContent) {
        this.commentContent = commentContent;
    }

    public static Comment creatComment(String content,Member member, Post post){
        return Comment.builder().commentContent(content).member(member).post(post).build();
    }
}
