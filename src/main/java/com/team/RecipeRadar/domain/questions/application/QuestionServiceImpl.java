package com.team.RecipeRadar.domain.questions.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.questions.dao.AnswerRepository;
import com.team.RecipeRadar.domain.questions.dao.QuestionRepository;
import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import com.team.RecipeRadar.domain.questions.dto.QuestionAllResponse;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionRequest;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import com.team.RecipeRadar.global.exception.ex.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.NoSuchErrorType;
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
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final S3UploadService s3UploadService;
    private final ImgRepository imgRepository;
    private final MemberRepository memberRepository;
    private final NotificationService notificationService;
    private final AnswerRepository answerRepository;

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
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
     * 문의사항 전체 보기
     */
    @Override
    @Transactional(readOnly = true)
    public QuestionAllResponse allQuestion(Long lasId, QuestionType questionType, QuestionStatus questionStatus, Pageable pageable) {
        Slice<QuestionDto> allQuestion = questionRepository.getAllQuestion(lasId, questionType, questionStatus, pageable);
        return new QuestionAllResponse(allQuestion.hasNext(),allQuestion.getContent());
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

    private void validateAdminAccess(Member member) {
        if (!member.getRoles().contains(ROLE_ADMIN)) {
            throw new BadRequestException("관리자만 접근 가능 가능합니다.");
        }
    }

    private void saveImageIfProvided(MultipartFile file, Question question) {
        if (file!=null) {
         s3UploadService.uploadFile(file,List.of(question));
        }
    }
}