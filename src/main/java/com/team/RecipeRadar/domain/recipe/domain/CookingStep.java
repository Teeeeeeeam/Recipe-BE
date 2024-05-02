package com.team.RecipeRadar.domain.recipe.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CookingStep {


    @Id@GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "cook_step_id")
    private Long id;

    @Column(length = 5000)
    private String steps;

    @OneToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;
}
