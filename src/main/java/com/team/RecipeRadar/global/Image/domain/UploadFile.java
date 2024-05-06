package com.team.RecipeRadar.global.Image.domain;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "img_id")
    private Long id;

    private String originFileName;      // 실제 파일명
    private String storeFileName;       // DB에 저장될 파일명

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipe_id")
    private Recipe recipe;

    public UploadFile(String uploadFileName, String storeFileName, Recipe recipe) {
        this.originFileName = uploadFileName;
        this.storeFileName = storeFileName;
        this.recipe = recipe;
    }

    public UploadFile(String originFileName, String storeFileName) {
        this.originFileName = originFileName;
        this.storeFileName = storeFileName;
    }
}
