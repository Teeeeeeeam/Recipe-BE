package com.team.RecipeRadar.domain.qna.application.admin;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.qna.dao.answer.AnswerRepository;
import com.team.RecipeRadar.domain.qna.dao.question.QuestionRepository;
import com.team.RecipeRadar.domain.qna.domain.Answer;
import com.team.RecipeRadar.domain.qna.domain.Question;
import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import com.team.RecipeRadar.domain.qna.dto.response.QuestionAllResponse;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.domain.email.event.NoneQuestionMailEvent;
import com.team.RecipeRadar.domain.email.event.QuestionMailEvent;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.team.RecipeRadar.domain.qna.domain.AnswerType.EMAIL;
import static com.team.RecipeRadar.domain.qna.domain.QuestionType.GENERAL_INQUIRY;

@Service
@Transactional
@RequiredArgsConstructor
public class AdminQnAServiceImpl implements AdminQnAService {

    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final NotificationService notificationService;
    private final ApplicationEventPublisher eventPublisher;
    private final AnswerRepository answerRepository;


    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    /**
     * 문의사항 상세보기
     */
    @Override
    @Transactional(readOnly = true)
    public QuestionDto detailAdminQuestion(Long questionId, Long memberId) {
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));
        validateAdminAccess(member);
        return questionRepository.details(questionId);
    }

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
     * 문의사항 전체 보기
     */
    @Override
    @Transactional(readOnly = true)
    public QuestionAllResponse allQuestion(Long lasId, QuestionType questionType, QuestionStatus questionStatus, Pageable pageable) {
        Slice<QuestionDto> allQuestion = questionRepository.getAllQuestion(lasId, questionType, questionStatus, pageable);
        return new QuestionAllResponse(allQuestion.hasNext(),allQuestion.getContent());
    }

    private void validateAdminAccess(Member member) {
        if (!member.getRoles().contains(ROLE_ADMIN)) {
            throw new UnauthorizedException("관리자만 접근 가능 가능합니다.");
        }
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
