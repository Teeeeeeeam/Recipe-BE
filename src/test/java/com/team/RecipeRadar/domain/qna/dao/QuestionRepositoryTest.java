package com.team.RecipeRadar.domain.questions.dao;

import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Import(QueryDslConfig.class)
@Transactional
@ActiveProfiles("test")
@Slf4j
class QuestionRepositoryTest {

    @Autowired QuestionRepository questionRepository;
    @Autowired ImgRepository imgRepository;

    @Value("${S3.URL}")
    private String s3URL;
    
    @Test
    @DisplayName("문의 사항 상세조회")
    void details_withImage() {
        Question question = new Question();
        questionRepository.save(question);

        UploadFile uploadFile = UploadFile.builder().storeFileName("testImage.jpg").question(question).build();
        imgRepository.save(uploadFile);

        QuestionDto result = questionRepository.details(question.getId());
        
        assertThat(result).isNotNull();
        assertThat(result.getImgUrl()).isEqualTo(s3URL + "testImage.jpg");
    }

    @Test
    @DisplayName("이미지 없을떄 조회")
    void details_withoutImage() {
        Question question = new Question();
        questionRepository.save(question);
        
        QuestionDto result = questionRepository.details(question.getId());
        
        assertThat(result).isNotNull();
        assertThat(result.getImgUrl()).isNull();
    }

    @Test
    @DisplayName("문의 사항 예외")
    void details_questionNotFound() {
        assertThrows(NoSuchElementException.class, () -> questionRepository.details(999L));
    }

}