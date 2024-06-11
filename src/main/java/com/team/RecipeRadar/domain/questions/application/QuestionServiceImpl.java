package com.team.RecipeRadar.domain.questions.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.questions.dao.AnswerRepository;
import com.team.RecipeRadar.domain.questions.dao.QuestionRepository;
import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import com.team.RecipeRadar.domain.questions.dto.QuestionAllResponse;
import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionRequest;
import com.team.RecipeRadar.global.Image.dao.ImgRepository;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.aws.S3.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.NoSuchElementException;

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
    /**
     * 계정이 정지되었을때 문의사항 보낼떄 사용
     */
    @Override
    public void account_Question(QuestionRequest questionRequest, MultipartFile file) {
        Question question = buildQuestion(questionRequest);
        
        setMemberIfProvided(null, question);

        Question savedQuestion = questionRepository.save(question);

        notificationService.sendAdminNotification(question,null);

        saveImageIfProvided(file, savedQuestion);
    }

    /**
     * 일반 문의사항일때 문의사항 로직
     */
    @Override
    public void general_Question(QuestionRequest questionRequest,Long memberId, MultipartFile file) {
        Question question = buildQuestion(questionRequest);  // 질문 생성

        setMemberIfProvided(memberId,question);        // 사용자 설정

        Question savedQuestion = questionRepository.save(question);  // 질문 저장

        notificationService.sendAdminNotification(question,savedQuestion.getMember().getNickName());

        saveImageIfProvided(file, savedQuestion);          // 사진이 제공된 경우 저장
    }

    /**
     * 문의사항 상세보기
     * @param questionId
     * @param loginId
     * @return
     */
    @Override
    @Transactional(readOnly = true)
    public QuestionDto detailAdmin_Question(Long questionId,String loginId) {
        Member member = memberRepository.findByLoginId(loginId);

        if(member.getRoles().equals("ROLE_ADMIN")){
            return questionRepository.details(questionId);
        }else
            throw new BadRequestException("관리자만 접근 가능 가능합니다.");
    }

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
    public void deleteQuestions(List<Long> ids, MemberDto memberDto) {
        List<Question> questions = questionRepository.findAllById(ids);
        if (questions.isEmpty()) throw new BadRequestException("해당 문의사항이 존재하지 않습니다.");
        
        boolean isMember = questions.get(0).getMember().getId().equals(memberDto.getId());

        if(isMember) {
            for (Question question : questions) {
                UploadFile byQuestionId = imgRepository.findByQuestionId(question.getId());
                if (byQuestionId != null) {
                    imgRepository.deleteById(byQuestionId.getId());
                    s3UploadService.deleteFile(byQuestionId.getStoreFileName());
                }
                answerRepository.deleteByQuestionId(question.getId());
                questionRepository.delete(question);
            }
        }else throw new BadRequestException("작성자만 삭제 가능합니다.");
    }

    private Question buildQuestion(QuestionRequest questionRequest) {
        return Question.builder()
                .question_content(questionRequest.getQuestion_content())
                .title(questionRequest.getTitle())
                .answer(questionRequest.getAnswer())
                .status(QuestionStatus.PENDING)
                .answer_email(questionRequest.getAnswer_email())
                .questionType(questionRequest.getQuestionType())
                .build();
    }

    private void setMemberIfProvided(Long memberId ,Question question) {
        if (memberId != null) {
            Member member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new BadRequestException("사용자를 찾을 수 없습니다."));
            question.setMember(member);
        }
    }

    private void saveImageIfProvided(MultipartFile file, Question question) {
        if (file!=null) {
            String originalFilename = file.getOriginalFilename();
            String storeName = s3UploadService.uploadFile(file);
            UploadFile uploadFile = UploadFile.builder()
                    .originFileName(originalFilename)
                    .storeFileName(storeName)
                    .question(question)
                    .build();
            imgRepository.save(uploadFile);
        }
    }
}