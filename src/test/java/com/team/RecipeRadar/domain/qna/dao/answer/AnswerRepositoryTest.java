package com.team.RecipeRadar.domain.qna.dao.answer;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.qna.dao.question.QuestionRepository;
import com.team.RecipeRadar.domain.qna.domain.Answer;
import com.team.RecipeRadar.domain.qna.domain.Question;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class AnswerRepositoryTest {

    @Autowired AnswerRepository answerRepository;
    @Autowired QuestionRepository questionRepository;
    @Autowired MemberRepository memberRepository;


    private Question question;
    private Answer answer;

    @BeforeEach
    void setUp(){
        question = Question.builder().title("문의사항").build();
        questionRepository.save(question);
        answer = Answer.builder().question(question).answerTitle("답변").build();
        answerRepository.save(answer);

    }

    @Test
    @DisplayName("문의사항 아이디로 답변 삭제")
    void deleteAnswerByQuestionId(){
        List<Answer> before = answerRepository.findAll();
        answerRepository.deleteByQuestionId(question.getId());
        List<Answer> after = answerRepository.findAll();
        assertThat(before).hasSize(1);
        assertThat(after).hasSize(0);
    }
    
    @Test
    @DisplayName("문의사항의 작성된 질문 응답 조회")
    void qna(){
        QuestionDto questionDto = answerRepository.viewResponse(question.getId());
        assertThat(questionDto).isNotNull();
        assertThat(questionDto.getAnswer().getAnswerTitle()).isEqualTo("답변");
    }
}