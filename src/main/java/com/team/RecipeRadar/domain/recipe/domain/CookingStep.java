package com.team.RecipeRadar.domain.recipe.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CookingStep {


    @Id@GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Column(name = "cook_step_id")
    private Long id;

    @Column(length = 5000)
    private String steps;

    @ManyToOne
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public void update(String steps){
        this.steps = steps;
    }

    public static CookingStep createCookingStep(Recipe recipe,String steps){
        return CookingStep.builder().recipe(recipe).steps(steps).build();
    }
}
