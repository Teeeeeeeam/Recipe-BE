package com.team.RecipeRadar.domain.questions.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.questions.dao.QuestionRepository;
import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.questions.domain.QuestionStatus;
import com.team.RecipeRadar.domain.questions.dto.QuestionAnswerRequest;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionRequest;
import com.team.RecipeRadar.global.Image.dao.ImgRepository;
import com.team.RecipeRadar.global.Image.domain.UploadFile;
import com.team.RecipeRadar.global.aws.S3.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    /**
     * 계정이 정지되었을때 문의사항 보낼떄 사용
     */
    @Override
    public void account_Question(QuestionRequest questionRequest, MultipartFile file) {
        Question question = buildQuestion(questionRequest);
        
        setMemberIfProvided(questionRequest, question);

        Question savedQuestion = questionRepository.save(question);

        notificationService.sendAdminNotification(question,null);

        saveImageIfProvided(file, savedQuestion);
    }

    /**
     * 일반 문의사항일때 문의사항 로직
     */
    @Override
    public void general_Question(QuestionRequest questionRequest, MultipartFile file) {
        Question question = buildQuestion(questionRequest);  // 질문 생성

        setMemberIfProvided(questionRequest, question);        // 사용자 설정

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

    private void setMemberIfProvided(QuestionRequest questionRequest, Question question) {
        if (questionRequest.getMemberId() != null) {
            Member member = memberRepository.findById(questionRequest.getMemberId())
                    .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
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