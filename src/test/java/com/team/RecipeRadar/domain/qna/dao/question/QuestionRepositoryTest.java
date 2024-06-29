package com.team.RecipeRadar.domain.qna.dao.question;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.qna.dao.answer.AnswerRepository;
import com.team.RecipeRadar.domain.qna.domain.Answer;
import com.team.RecipeRadar.domain.qna.domain.Question;
import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;


@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class QuestionRepositoryTest {

    @Autowired QuestionRepository questionRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired AnswerRepository answerRepository;
    @Autowired ImgRepository imgRepository;

    @Value("${S3.URL}")
    private String s3URL;

    private List<Question> questions;
    private List<Member> members;

    @BeforeEach
    void setUp(){
        members = List.of(
                Member.builder().nickName("닉네임").loginId("아이디").username("실명").build(),
                Member.builder().nickName("닉네임").loginId("아이디").username("실명").build());

        memberRepository.saveAll(members);

        questions = List.of(
                Question.builder().questionContent("내용").member(members.get(0)).questionType(QuestionType.GENERAL_INQUIRY).status(QuestionStatus.PENDING).build(),
                Question.builder().questionContent("내용").member(members.get(0)).questionType(QuestionType.GENERAL_INQUIRY).status(QuestionStatus.PENDING).build(),
                Question.builder().questionContent("내용").member(members.get(1)).questionType(QuestionType.GENERAL_INQUIRY).status(QuestionStatus.COMPLETED).build());

        questionRepository.saveAll(questions);
        Answer answer = Answer.builder().answerContent("응답").answerTitle("응답제목").question(questions.get(2)).build();
        answerRepository.save(answer);
    }


    @Test
    @DisplayName("문의 사항 상세조회")
    void details_withImage() {
        UploadFile uploadFile = UploadFile.builder().storeFileName("testImage.jpg").question(questions.get(0)).build();
        imgRepository.save(uploadFile);

        QuestionDto result = questionRepository.details(questions.get(0).getId());
        
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
        assertThrows(NoSuchDataException.class, () -> questionRepository.details(999L));
    }

    @Test
    @DisplayName("사용자 ID 문의사항 전체삭제")
    void deleteAllByMemberId(){
        List<Question> before = questionRepository.findAll();
        questionRepository.deleteAllByMemberId(members.get(0).getId());
        List<Question> after = questionRepository.findAll();

        assertThat(before).hasSize(3);
        assertThat(after).hasSize(1);
    }
    
    @Test
    @DisplayName("전체 문의사항 조회")
    void getAllQuestion(){
        Slice<QuestionDto> allQuestion = questionRepository.getAllQuestion(null, null, null, Pageable.ofSize(3));
        Slice<QuestionDto> answerQuestion = questionRepository.getAllQuestion(null, null, QuestionStatus.COMPLETED, Pageable.ofSize(3));
        assertThat(allQuestion.getContent()).hasSize(3);
        assertThat(answerQuestion.getContent()).hasSize(1);
    }
    
    @Test
    @DisplayName("사용자가 작성한 문의사항 조회")
    void userAllQuestion(){
        Slice<QuestionDto> userAllQuestion = questionRepository.getUserAllQuestion(null, members.get(0).getId(), null, null, Pageable.ofSize(1));
        assertThat(userAllQuestion.getContent()).hasSize(1);
        assertThat(userAllQuestion.hasNext()).isTrue();
    }
}