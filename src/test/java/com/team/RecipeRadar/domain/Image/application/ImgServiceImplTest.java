package com.team.RecipeRadar.domain.Image.application;

import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class ImgServiceImplTest {


    @Mock
    ImgRepository imgRepository;

    @InjectMocks
    ImgServiceImpl imgService;

    @Test
    @DisplayName("이미지 저장 테스트")
    void saveImg(){

        Recipe recipe = Recipe.builder().id(1l).title("제목").build();
        UploadFile uploadFile = UploadFile.builder().id(1l).originFileName("실제파일명").storeFileName("저장될 파일명").recipe(recipe).build();

        when(imgRepository.save(uploadFile)).thenReturn(uploadFile);

        imgService.saveRecipeImage(recipe,uploadFile);

        verify(imgRepository,times(1)).save(uploadFile);        // imgRepository를 한번 호출하는지
        assertThat(uploadFile.getRecipe()).isEqualTo(recipe);                         // 저장된 파일의 레시피가 같은지
    }
}