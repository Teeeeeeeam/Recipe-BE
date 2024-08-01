package com.team.RecipeRadar.domain.like.domain;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import lombok.*;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "post_id"),
        @Index(columnList = "member_id"),
})
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "member")
public class PostLike {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id",foreignKey =  @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    public static PostLike createPostLIke(Post post, Member member){
        return PostLike.builder().post(post).member(member).build();
    }
}
