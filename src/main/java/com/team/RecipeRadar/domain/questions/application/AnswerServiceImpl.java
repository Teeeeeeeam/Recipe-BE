package com.team.RecipeRadar.domain.questions.application;

import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.notification.domain.NotificationType;
import com.team.RecipeRadar.domain.questions.dao.AnswerRepository;
import com.team.RecipeRadar.domain.questions.dao.QuestionRepository;
import com.team.RecipeRadar.domain.questions.domain.Answer;
import com.team.RecipeRadar.domain.questions.domain.AnswerType;
import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;
import com.team.RecipeRadar.global.email.event.NoneQuestionMailEvent;
import com.team.RecipeRadar.global.email.event.QuestionMailEvent;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.team.RecipeRadar.domain.questions.domain.AnswerType.*;
import static com.team.RecipeRadar.domain.questions.domain.QuestionType.*;


@Service
@Transactional
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService{

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;


    @Override
    public void question_answer(Long questId,QuestionAnswerRequest questionAnswerRequest,String adminNickName) {

        Question question = questionRepository.findById(questId).orElseThrow(() -> new BadRequestException("문의사항이 존재하지 않습니다."));
        question.updateStatus(questionAnswerRequest.getQuestionStatus());   //문의사항 진행을 답변완료 변경

        //일반 문의 사항의 답변의 대해서는 알림이 가도록 사용
        if(question.getQuestionType().equals(GENERAL_INQUIRY)) {
            notificationService.complete_question(question, adminNickName);
            if(question.getAnswer().equals(EMAIL)){     //이메일 선택시 이메일로 전송
                eventPublisher.publishEvent(new QuestionMailEvent(question.getAnswer_email()));
            }
        }else{  //계정일떄는 이메일 이벤트가 가도록
            if(question.getAnswer().equals(EMAIL)){     //이메일 선택시 이메일로 전송
                eventPublisher.publishEvent(new NoneQuestionMailEvent(question.getAnswer_email(),questionAnswerRequest.getAnswer_title(),questionAnswerRequest.getAnswer_content()));
            }
        }

        Answer build = Answer.builder()
                .question(question)
                .answerContent(questionAnswerRequest.getAnswer_content())
                .answerTitle(questionAnswerRequest.getAnswer_title()).build();
        answerRepository.save(build);
    }
}
