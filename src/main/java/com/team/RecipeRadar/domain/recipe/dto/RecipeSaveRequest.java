package com.team.RecipeRadar.domain.recipe.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RecipeSaveRequest {

    @NotEmpty(message = "레시피 제목을 작성해주세요")
    private String title;
    @NotEmpty(message = "레시피의 난이도를 작성해주세요")
    private String cookLevel;
    @NotEmpty(message = "레시피의 인원수를 작성해주세요")
    private String people;
    @NotEmpty(message = "레시피의 재료를 입력해주세여")
    private String ingredients;
    @NotEmpty(message = "레시피의 조리 시간을 작성해주세요")
    private String cookTime;
    @NotEmpty(message = "레시피의 조리 순서를 작성해주세요")
    private List<String> cookSteps;

    private RecipeSaveRequest(String title, String cookLevel, String people, String ingredients, List<String> cookSteps) {
        this.title = title;
        this.cookLevel = cookLevel;
        this.people = people;
        this.ingredients = ingredients;
        this.cookSteps = cookSteps;
    }

    public static RecipeSaveRequest of(RecipeSaveRequest recipeSaveRequest){
        return new RecipeSaveRequest(recipeSaveRequest.getTitle(),recipeSaveRequest.getCookLevel(),recipeSaveRequest.getPeople(),recipeSaveRequest.getIngredients(),recipeSaveRequest.getCookSteps());
    }
}
