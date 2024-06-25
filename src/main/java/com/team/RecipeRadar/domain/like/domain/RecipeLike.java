package com.team.RecipeRadar.domain.like.domain;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "recipe_id"),
        @Index(columnList = "member_id"),
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecipeLike {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recipe_like_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;


    public static RecipeLike createRecipeLike(Member member, Recipe recipe){
        return RecipeLike.builder().member(member).recipe(recipe).build();
    }
}
