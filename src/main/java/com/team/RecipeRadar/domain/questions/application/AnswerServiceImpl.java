package com.team.RecipeRadar.domain.questions.application;

import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.questions.dao.AnswerRepository;
import com.team.RecipeRadar.domain.questions.dao.QuestionRepository;
import com.team.RecipeRadar.domain.questions.domain.Answer;
import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.global.event.email.NoneQuestionMailEvent;
import com.team.RecipeRadar.global.event.email.QuestionMailEvent;
import com.team.RecipeRadar.global.exception.ex.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.team.RecipeRadar.domain.questions.domain.AnswerType.*;
import static com.team.RecipeRadar.domain.questions.domain.QuestionType.*;


@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class AnswerServiceImpl implements AnswerService{

    private final AnswerRepository answerRepository;
    private final QuestionRepository questionRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;


    /**
     * 문의사항의 답변을 등록하는 메서드
     * 문의사항 등록시 선택한 알림받기 상태의 따라 이메이로 알림이 전송된다.
     */
    @Override
    public void questionAnswer(Long questId, QuestionAnswerRequest questionAnswerRequest, String adminNickName) {

        Question question = getQuestion(questId);
        question.updateStatus(questionAnswerRequest.getQuestionStatus());   //문의사항 진행을 답변완료 변경
        handleNotificationAndEmail(questionAnswerRequest, adminNickName, question);
        answerRepository.save(Answer.createAanswer(questionAnswerRequest.getAnswerTitle(),questionAnswerRequest.getAnswerContent(),adminNickName,question));
    }

    /**
     * 작성한 문의사항의 답변
     * @param memberDto 현재 로그인한 사용자의 DTO
     * @param questionId 조회할 문의사항 ID
     * @return
     */
    @Override
    public QuestionDto viewResponse(MemberDto memberDto, Long questionId) {
        QuestionDto questionDto = answerRepository.viewResponse(questionId);

        if(questionDto.getMember() == null){
            throw new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER);
        }
        if(!memberDto.getId().equals(questionDto.getMember().getId()) && !memberDto.getRoles().equals("ROLE_ADMIN")){
            throw new UnauthorizedException("작성자만 열람 가능합니다.");
        }

        return questionDto;
    }

    private void handleNotificationAndEmail(QuestionAnswerRequest questionAnswerRequest, String adminNickName, Question question) {
        //일반 문의 사항의 답변의 대해서는 알림이 가도록 사용
        if(question.getQuestionType().equals(GENERAL_INQUIRY)) {
            notificationService.completeQuestion(question, adminNickName);
            if(question.getAnswer().equals(EMAIL)){     //이메일 선택시 이메일로 전송
                eventPublisher.publishEvent(new QuestionMailEvent(question.getAnswerEmail()));
            }
        }else{  //계정일떄는 이메일 이벤트가 가도록
            if(question.getAnswer().equals(EMAIL)){     //이메일 선택시 이메일로 전송
                eventPublisher.publishEvent(new NoneQuestionMailEvent(question.getAnswerEmail(),
                        questionAnswerRequest.getAnswerTitle(),
                        questionAnswerRequest.getAnswerContent()));
            }
        }
    }
    private Question getQuestion(Long questId) {
        return questionRepository.findById(questId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_QUESTION));
    }
}
