package com.team.RecipeRadar.domain.like.domain;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.*;

@Entity
@Slf4j
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PostLike {

    @Id
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "post_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;
    
}
