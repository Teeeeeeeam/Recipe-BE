package com.team.RecipeRadar.domain.Image.application;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.qna.domain.Question;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.exception.ex.img.ImageException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;

import java.util.List;

import static com.team.RecipeRadar.global.exception.ex.img.ImageErrorType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class S3UploadServiceTest {

    @Mock private AmazonS3 amazonS3;

    @Mock private ImgRepository imgRepository;

    @InjectMocks private S3UploadService s3UploadService;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    @Test
    @DisplayName("이미지 업로드 테스트")
    void testUploadFile() {
        MockMultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "test data".getBytes());

        List<Object> entities = List.of(new Recipe(), new Post(), new Notice(), new Question());
        when(imgRepository.save(any())).thenReturn(new UploadFile());

        when(amazonS3.putObject(any(PutObjectRequest.class))).thenReturn(any(PutObjectResult.class));

        String storedFileName = s3UploadService.uploadFile(file, entities);

        assertThat(storedFileName).isNotNull();
        assertThat(storedFileName.endsWith(".jpg")).isTrue();

        verify(imgRepository, times(1)).save(any());
        verify(amazonS3, times(1)).putObject(any(PutObjectRequest.class));
    }

    @Test
    @DisplayName("이미지 업로드 실패 테스트 - 빈 파일")
    void testUploadFileEmptyFile() {
        MockMultipartFile file = null;

        assertThatThrownBy(() -> s3UploadService.uploadFile(file, List.of()))
                .isInstanceOf(ImageException.class)
                .hasFieldOrPropertyWithValue("errorType", MISSING_PRIMARY_IMAGE);
    }

    @Test
    @DisplayName("이미지 업로드 실패 테스트 - 허용되지 않은 확장자")
    void testUploadFileInvalidExtension() {
        MockMultipartFile file = new MockMultipartFile("test.txt", "test.txt", "text/plain", "test data".getBytes());

        assertThatThrownBy(() -> s3UploadService.uploadFile(file,List.of()))
                .isInstanceOf(ImageException.class)
                .hasFieldOrPropertyWithValue("errorType",INVALID_IMAGE_FORMAT);
    }
    @Test
    @DisplayName("이미지 삭제 테스트")
    void testDeleteFile() {
        String uploadFileName = "test.jpg";

        doNothing().when(amazonS3).deleteObject(eq(bucket), eq(uploadFileName));

        s3UploadService.deleteFile(uploadFileName);

        verify(amazonS3, times(1)).deleteObject(eq(bucket), eq(uploadFileName));
    }
    @Test
    @DisplayName("이미지 삭제 실패 테스트")
    void testDeleteFileSdkClientException() {
        doThrow(SdkClientException.class).when(amazonS3).deleteObject(eq(bucket), anyString());

        assertThatThrownBy(() -> s3UploadService.deleteFile("이미지 명"))
                .isInstanceOf(ImageException.class)
                .hasFieldOrPropertyWithValue("errorType", UPLOAD_FAILS);
    }
}
