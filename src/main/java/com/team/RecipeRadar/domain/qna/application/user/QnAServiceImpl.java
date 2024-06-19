package com.team.RecipeRadar.domain.qna.application.user;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.qna.dao.answer.AnswerRepository;
import com.team.RecipeRadar.domain.qna.dao.question.QuestionRepository;
import com.team.RecipeRadar.domain.qna.domain.Question;
import com.team.RecipeRadar.domain.qna.domain.QuestionStatus;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import com.team.RecipeRadar.domain.qna.dto.response.QuestionAllResponse;
import com.team.RecipeRadar.domain.qna.dto.QuestionDto;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionRequest;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.nosuch.NoSuchErrorType;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class QnAServiceImpl implements QnAService {

    private final QuestionRepository questionRepository;
    private final S3UploadService s3UploadService;
    private final ImgRepository imgRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final AnswerRepository answerRepository;

    /**
     * 계정이 정지되었을때 문의사항 보낼떄 사용
     */
    @Override
    public void accountQuestion(QuestionRequest questionRequest, MultipartFile file) {
        Question question = getQuestion(questionRequest);
        setMemberIfProvided(null, question);

        Question savedQuestion = questionRepository.save(question);

        notificationService.sendAdminNotification(question,null);

        saveImageIfProvided(file, savedQuestion);
    }

    /**
     * 일반 문의사항일때 문의사항 로직
     */
    @Override
    public void generalQuestion(QuestionRequest questionRequest, Long memberId, MultipartFile file) {
        Question question = getQuestion(questionRequest);

        setMemberIfProvided(memberId,question);        // 사용자 설정

        Question savedQuestion = questionRepository.save(question);  // 질문 저장

        notificationService.sendAdminNotification(question,savedQuestion.getMember().getNickName());

        saveImageIfProvided(file, savedQuestion);          // 사진이 제공된 경우 저장
    }

    /* 사용자 파에지에서 사용자가 작성한 문의사항의 대해서 조회한 데이터를 response로 변환 */
    @Override
    @Transactional(readOnly = true)
    public QuestionAllResponse allUserQuestion(Long lasId, Long memberId, QuestionType questionType, QuestionStatus questionStatus, Pageable pageable) {
        Slice<QuestionDto> allQuestion = questionRepository.getUserAllQuestion(lasId,memberId, questionType, questionStatus, pageable);
        return new QuestionAllResponse(allQuestion.hasNext(),allQuestion.getContent());
    }
    /* 사용자가 작성한 문의사항을 삭제한다. 단일 및 일괄 삭제 가능 */

    @Override
    public void deleteQuestions(List<Long> ids, Long memberId) {
        List<Question> questions = questionRepository.findAllById(ids);
        if (questions.isEmpty()) throw new NoSuchDataException(NoSuchErrorType.NO_SUCH_QUESTION);

        boolean isMember = questions.stream()
                .anyMatch(question -> question.getMember().getId().equals(memberId));

        if (isMember) {
            questions.forEach(question -> {
                UploadFile uploadFile = imgRepository.findByQuestionId(question.getId());
                if (uploadFile != null) {
                    imgRepository.deleteById(uploadFile.getId());
                    s3UploadService.deleteFile(uploadFile.getStoreFileName());
                }
                answerRepository.deleteByQuestionId(question.getId());
                questionRepository.delete(question);
            });
        } else
            throw new UnauthorizedException("작성자만 삭제 가능합니다.");

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

    private static Question getQuestion(QuestionRequest questionRequest) {
        return  Question.createQuestion(questionRequest.getTitle(), questionRequest.getQuestionContent(), questionRequest.getAnswer(), questionRequest.getAnswerEmail(), questionRequest.getQuestionType());
    }

    private void setMemberIfProvided(Long memberId ,Question question) {
        if (memberId != null) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new NoSuchDataException(NoSuchErrorType.NO_SUCH_MEMBER));
            question.setMember(member);
        }
    }


    private void saveImageIfProvided(MultipartFile file, Question question) {
        if (file!=null) {
         s3UploadService.uploadFile(file,List.of(question));
        }
    }
}