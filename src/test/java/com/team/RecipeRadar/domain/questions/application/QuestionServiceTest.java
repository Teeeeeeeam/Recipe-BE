package com.team.RecipeRadar.domain.questions.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.questions.dao.QuestionRepository;
import com.team.RecipeRadar.domain.questions.domain.Question;
import com.team.RecipeRadar.domain.questions.dto.QuestionDto;
import com.team.RecipeRadar.domain.questions.dto.QuestionRequest;
import com.team.RecipeRadar.domain.Image.dao.ImgRepository;
import com.team.RecipeRadar.domain.Image.domain.UploadFile;
import com.team.RecipeRadar.domain.Image.application.S3UploadService;
import com.team.RecipeRadar.global.exception.ex.BadRequestException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

import static com.team.RecipeRadar.domain.questions.domain.AnswerType.*;
import static com.team.RecipeRadar.domain.questions.domain.QuestionStatus.*;
import static com.team.RecipeRadar.domain.questions.domain.QuestionType.*;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {

    @Mock QuestionRepository questionRepository;
    @Mock S3UploadService s3UploadService;
    @Mock ImgRepository imgRepository;
    @Mock MemberRepository memberRepository;
    @Mock NotificationService notificationService;

    @InjectMocks QuestionServiceImpl questionService;

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
                .isInstanceOf(BadRequestException.class)
                .hasMessage("관리자만 접근 가능 가능합니다.");
    }

}