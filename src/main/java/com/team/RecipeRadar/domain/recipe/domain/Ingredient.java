package com.team.RecipeRadar.domain.recipe.domain;


import lombok.*;

import javax.persistence.*;


@Entity
@NoArgsConstructor
@Builder
@AllArgsConstructor
@ToString(exclude = {"ingredientAndQuantity"})
@Table(indexes = @Index(columnList = "ingredients"))
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ingredient_id")
    private Long id;

    @Column(length = 4000)
    @Setter
    private String ingredients;         // 재료

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}