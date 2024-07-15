package com.team.RecipeRadar.domain.qna.application.admin;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.qna.dao.answer.AnswerRepository;
import com.team.RecipeRadar.domain.qna.dao.question.QuestionRepository;
import com.team.RecipeRadar.domain.qna.domain.*;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionAnswerRequest;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminQnAServiceTest {

    @Mock QuestionRepository questionRepository;
    @Mock MemberRepository memberRepository;
    @Mock AnswerRepository answerRepository;
    @Mock NotificationService notificationService;
    @Mock ApplicationEventPublisher eventPublisher;

    
    @InjectMocks AdminQnAServiceImpl questionService;

    @Test
    @DisplayName("관리자만 문의사항 상세보기 접근 가능")
    void detailAdmin_Question() {
        Long questionId = 1L;
        String loginId = "admin";
        Member adminMember = Member.builder().id(1l).loginId(loginId).roles("ROLE_ADMIN").build();

        QuestionDto questionDto = new QuestionDto();

        when(memberRepository.findById(1l)).thenReturn(Optional.of(adminMember));
        when(questionRepository.details(questionId)).thenReturn(questionDto);

        QuestionDto result = questionService.detailAdminQuestion(questionId,1l);

        assertThat(result).isEqualTo(questionDto);
    }

    @Test
    @DisplayName("관리자만 문의사항 상세보기 접근 가능 - 예외 발생")
    void detailAdmin_Question_throwsException() {
        Long questionId = 1L;
        String loginId = "user";
        Member userMember = Member.builder().id(1l).loginId(loginId).roles("ROLE_USER").build();

        when(memberRepository.findById(1l)).thenReturn(Optional.of(userMember));

        assertThatThrownBy(() -> questionService.detailAdminQuestion(questionId, 1l))
                .isInstanceOf(UnauthorizedException.class)
                .hasMessage("관리자만 접근 가능 가능합니다.");
    }

    @Test
    @DisplayName("문의사항의 대한 답변 작성")
    void answerByQuestion(){
        Question question = Question.builder().id(1l).questionType(QuestionType.GENERAL_INQUIRY).answerEmail("email").answer(AnswerType.EMAIL).build();
        when(questionRepository.findById(anyLong())).thenReturn(Optional.of(question));

        when(answerRepository.save(any())).thenReturn(Answer.builder().id(1l).answerTitle("응답 제목").answerContent("응답 내용").question(question).build());

        QuestionAnswerRequest questionAnswerRequest = new QuestionAnswerRequest();
        questionAnswerRequest.setQuestionStatus(QuestionStatus.COMPLETED);
        questionAnswerRequest.setAnswerTitle("응답 제목");
        questionAnswerRequest.setAnswerContent("응답 내용");

        questionService.questionAnswer(question.getId(), questionAnswerRequest,"관리자");

        verify(answerRepository, times(1)).save(any());

        ArgumentCaptor<Object> eventCaptor = ArgumentCaptor.forClass(Object.class);
        verify(eventPublisher, times(1)).publishEvent(eventCaptor.capture());
    }
}