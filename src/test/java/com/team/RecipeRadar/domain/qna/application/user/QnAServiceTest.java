package com.team.RecipeRadar.domain.qna.application.user;

import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.qna.dao.answer.AnswerRepository;
import com.team.RecipeRadar.domain.qna.dao.question.QuestionRepository;
import com.team.RecipeRadar.domain.qna.domain.Question;
import com.team.RecipeRadar.domain.qna.dto.reqeust.QuestionRequest;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.team.RecipeRadar.domain.qna.domain.AnswerType.*;
import static com.team.RecipeRadar.domain.qna.domain.QuestionStatus.*;
import static com.team.RecipeRadar.domain.qna.domain.QuestionType.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class QnAServiceTest {

    @Mock QuestionRepository questionRepository;
    @Mock S3UploadService s3UploadService;
    @Mock MemberRepository memberRepository;
    @Mock NotificationService notificationService;
    @Mock AnswerRepository answerRepository;
    @Mock ImgRepository imgRepository;




    @InjectMocks QnAServiceImpl questionService;

    @Test
    @DisplayName("계정 정지 상태에서 문의사항 등록")
    void account_Question() {
        // given
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestionContent("내용");
        questionRequest.setTitle("제목");
        questionRequest.setAnswer(EMAIL);
        questionRequest.setAnswerEmail("example@example.com");
        questionRequest.setQuestionType(ACCOUNT_INQUIRY);

        MultipartFile file = mock(MultipartFile.class);
        when(s3UploadService.uploadFile(eq(file),anyList())).thenReturn("uploaded/test.jpg");

        Question question = Question.builder()
                .questionContent("내용")
                .title("제목")
                .answer(EMAIL)
                .status(PENDING)
                .answerEmail("example@example.com")
                .questionType(ACCOUNT_INQUIRY)
                .build();

        when(questionRepository.save(any(Question.class))).thenReturn(question);

        questionService.accountQuestion(questionRequest, file);

        verify(questionRepository, times(1)).save(any(Question.class));
    }

    @Test
    @DisplayName("일반 문의사항 등록")
    void general_Question() {
        // given
        QuestionRequest questionRequest = new QuestionRequest();
        questionRequest.setQuestionContent("내용");
        questionRequest.setTitle("제목");
        questionRequest.setAnswer(EMAIL);
        questionRequest.setAnswerEmail("example@example.com");
        questionRequest.setQuestionType(ACCOUNT_INQUIRY);

        MultipartFile file = mock(MultipartFile.class);
        when(s3UploadService.uploadFile(eq(file),anyList())).thenReturn("uploaded/test.jpg");

        Member member = Member.builder().id(1l).nickName("닉네임").build();
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        Question question = Question.builder()
                .questionContent("내용")
                .title("제목")
                .answer(EMAIL)
                .status(PENDING)
                .answerEmail("example@example.com")
                .questionType(ACCOUNT_INQUIRY)
                .member(member)
                .build();
        when(questionRepository.save(any(Question.class))).thenReturn(question);

        // when
        questionService.generalQuestion(questionRequest, 1l,file);

        // then
        verify(questionRepository, times(1)).save(any(Question.class));
    }
}