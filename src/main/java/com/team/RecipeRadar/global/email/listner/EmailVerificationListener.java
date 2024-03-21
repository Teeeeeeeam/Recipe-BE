package com.team.RecipeRadar.global.email.listner;

import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.email.application.JoinEmailService;
import com.team.RecipeRadar.global.event.MemberJoinEmailEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Base64;

@Component
@Slf4j
@RequiredArgsConstructor
public class EmailVerificationListener implements ApplicationListener<MemberJoinEmailEvent> {

    private final JoinEmailService emailService; // 회원가입 이메일 서비스
    private final JavaMailSender mailSender; // 이메일 발송을 위한 JavaMailSender

    // MemberJoinEmailEvent를 처리하는 메서드
    @Override
    public void onApplicationEvent(MemberJoinEmailEvent event) {
        Member member = event.getMember(); // 이벤트에서 회원 정보를 가져옴
        String loginId = member.getLoginId(); // 회원 로그인 ID
        String verification = emailService.generateVerification(loginId); // 로그인 ID를 기반으로 인증 코드 생성
        String email = event.getMember().getEmail(); // 회원 이메일 주소

        // 이메일 메시지 생성
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("자유 게시판 가입 인증 안내입니다."); // 이메일 제목 설정
        message.setText(getText(member, verification)); // 이메일 내용 설정
        message.setFrom("wlwhsrjaeka@naver.com"); // 발신자 이메일 주소 설정
        message.setTo(email); // 수신자 이메일 주소 설정
        mailSender.send(message); // 이메일 발송
    }

    // 이메일 내용 생성 메서드
    private String getText(Member member, String verificationId) {
        String encodedId = new String(Base64.getEncoder().encode(verificationId.getBytes())); // 인증 코드를 Base64로 인코딩
        StringBuffer buffer = new StringBuffer();
        buffer.append("받는이 ").append(member.getUsername()).append(" ").append(System.lineSeparator()).append(System.lineSeparator()); // 이메일 받는이 정보 추가
        buffer.append("회원 가입을 진행하기 위해서 아래절차를 따라해주세요. "); // 회원 가입 안내 메시지 추가

        buffer.append("링크에 접속해 회원가입 인증을 진행해주시기 바랍니다.: "); // 인증 링크 안내 메시지 추가
        buffer.append("http://localhost:8080/verify/email?id=").append(encodedId); // 인증 링크 추가
        log.info("http://localhost:8080/verify/email?id={}", encodedId); // 로그에 인증 링크 정보 출력
        buffer.append(System.lineSeparator()).append(System.lineSeparator()); // 공백 라인 추가
        buffer.append("Regards,").append(System.lineSeparator()).append("나만의 냉장고"); // 문서 마무리 부분 추가
        return buffer.toString(); // 완성된 이메일 내용 반환
    }
}
