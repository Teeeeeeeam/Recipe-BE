package com.team.RecipeRadar.domain.Image.dao;

import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.notice.dao.NoticeRepository;
import com.team.RecipeRadar.domain.notice.domain.Notice;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.qna.dao.question.QuestionRepository;
import com.team.RecipeRadar.domain.qna.domain.Question;
import com.team.RecipeRadar.domain.recipe.dao.recipe.RecipeRepository;
import com.team.RecipeRadar.domain.recipe.domain.Recipe;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@Import(QueryDslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class ImgRepositoryTest {

    @Autowired ImgRepository imgRepository;
    @Autowired PostRepository postRepository;
    @Autowired RecipeRepository recipeRepository;
    @Autowired NoticeRepository noticeRepository;
    @Autowired QuestionRepository questionRepository;
    @Autowired EntityManager em;

    private List<UploadFile> uploadFiles;
    private Recipe recipe;
    private Post post;
    private Question question;
    private Notice notice;

    @BeforeEach
    void setUp(){
        recipe =Recipe.builder().title("레시피").build();
        recipeRepository.save(recipe);
        
        post =Post.builder().postTitle("게시글 제목").recipe(recipe).build();
        postRepository.save(post);

        question = Question.builder().title("문의사항").build();
        questionRepository.save(question);
        
        notice = Notice.builder().noticeTitle("공지사항").noticeContent("내용").build();
        noticeRepository.save(notice);

        //동일한 레시피로 등록된 이미지 파일 저장
        uploadFiles = List.of(
                UploadFile.builder().originFileName("레시피 이미지").storeFileName("서버의 이미지명").recipe(recipe).build(),
                UploadFile.builder().originFileName("레시피의 게시글 이미지").storeFileName("서버의 이미지명").recipe(recipe).post(post).build(),
                UploadFile.builder().originFileName("문의사항 이미지").storeFileName("서버의 이미지명").question(question).build(),
                UploadFile.builder().originFileName("공지사항 이미지").storeFileName("서버의 이미지명").notice(notice).build()
        );

        imgRepository.saveAll(uploadFiles);
    }

    @Test
    @DisplayName("레시피 이미지만 조회하는 테스트")
    void findUploadFileByRecipeIdAndPostNull() {
        Optional<UploadFile> uploadFile = imgRepository.findUploadFileByRecipeIdAndPostNull(recipe.getId());

        assertThat(uploadFile).isNotEmpty();
        assertThat(uploadFile.get().getOriginFileName()).isEqualTo("레시피 이미지");
    }

    @Test
    @DisplayName("문의사항의 이미지를 조회")
    void findByQuestionId() {
        UploadFile uploadFile = imgRepository.findByQuestionId(question.getId());
        assertThat(uploadFile).isNotNull();
        assertThat(uploadFile.getOriginFileName()).isEqualTo("문의사항 이미지");
    }

    @Test
    @DisplayName("게시글의 이미지만 삭제하는 테스트")
    void deleteUploadFileByPostIdAndRecipeId() {
        imgRepository.deleteUploadFileByPostIdAndRecipeId(post.getId(),recipe.getId());
        em.flush();

        List<UploadFile> all = imgRepository.findAll();
        assertThat(all).hasSize(3);
    }

    @Test
    @DisplayName("공지사항 조회 테스트")
    void findByNoticeId() {
        UploadFile uploadFile = imgRepository.findByNoticeId(notice.getId());
        assertThat(uploadFile.getOriginFileName()).isEqualTo("공지사항 이미지");
    }

    @Test
    @DisplayName("게시글 이미지 조회")
    void findByPostId() {
        UploadFile uploadFile = imgRepository.findByPostId(post.getId());
        assertThat(uploadFile.getOriginFileName()).isEqualTo("레시피의 게시글 이미지");
    }

    @Test
    @DisplayName("공지사항 이미지 삭제")
    void deleteByNoticeId() {
        imgRepository.deleteByNoticeId(notice.getId());
        em.flush();

        List<UploadFile> all = imgRepository.findAll();
        assertThat(all).hasSize(3);
    }
}