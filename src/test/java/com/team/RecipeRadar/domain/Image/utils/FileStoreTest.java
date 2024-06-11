package com.team.RecipeRadar.domain.Image.utils;

import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.Image.utils.FileStore;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import static org.assertj.core.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
@Slf4j
class FileStoreTest {

    @InjectMocks
    FileStore fileStore;


    @Test
    @DisplayName("파일 저장 테스트")
    void FileStore() throws Exception {
        
        String originFileName = "test.jpg";
        MockMultipartFile multipartFile = new MockMultipartFile("file", originFileName, "image", "test data".getBytes());

        UploadFile uploadFile = fileStore.storeFile(multipartFile);;

        assertThat(uploadFile.getOriginFileName()).isEqualTo(originFileName);
        assertThat(uploadFile.getStoreFileName()).isNotEmpty();
        assertThat(uploadFile.getStoreFileName().endsWith(".jpg")).isTrue();
    }
    
    @Test
    @DisplayName("사진 형식이 아닌 파일을 등록했을때 예외 체크")
    void storeFile_InvalidExtension() {
        String originFileName = "test.aa";
        MockMultipartFile multipartFile = new MockMultipartFile("file", originFileName, "image", "test data".getBytes());

        assertThatThrownBy(() -> fileStore.storeFile(multipartFile)).isInstanceOf(BadRequestException.class);
    }

    
    @Test
    @DisplayName("이미지 파일을 등록하지 않았을때")
    void Empty_Save_file(){
        MockMultipartFile multipartFile = new MockMultipartFile("file", null, "image", "test data".getBytes());
        assertThatThrownBy(() -> fileStore.storeFile(multipartFile)).isInstanceOf(BadRequestException.class);
    }

}