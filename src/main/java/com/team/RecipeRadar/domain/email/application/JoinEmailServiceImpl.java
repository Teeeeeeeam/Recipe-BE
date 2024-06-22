package com.team.RecipeRadar.domain.email.application;

import com.team.RecipeRadar.domain.email.dao.EmailVerificationRepository;
import com.team.RecipeRadar.domain.email.domain.EmailVerification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
@Qualifier("JoinEmail")
@Slf4j
public class JoinEmailServiceImpl implements MailService{

    private final JavaMailSender mailSender;
    private final EmailVerificationRepository emailVerificationRepository;
    private int code;
    @Value("${email}")
    private String emailFrom;

    /**
     * 이메일을 전송하기위한 메서드
     * 이멤일을 전송하면 인증번호를 반환 합니다.
     */
    public String sensMailMessage(String email){
        code=createCode();

        LocalDateTime expiredAt = LocalDateTime.now().plusMinutes(3);
        emailVerificationRepository.save(EmailVerification.creatEmailVerification(expiredAt,email,code));

        sendEmailVerification(email);

        return String.valueOf(code);
    }

    /**
     * 인증번호 생성 메서드
     * 임의의 난수 6자리를 생성합니다.
     */
    public Integer createCode(){
        Random random = new Random();
        int key =100000 + random.nextInt(900000);
        return key;
    }


    /**
     * 인증 번호 검증 메서드
     * 인증번호가 유효한지 검증하는 역활을 합니다.
     */
    public Map<String, Boolean> verifyCode(String email, int code) {
        EmailVerification emailVerification = emailVerificationRepository.findByEmailAndCode(email, code);

        boolean isVerified = emailVerification != null;
        boolean isExpired = emailVerification != null && isCodeValid(emailVerification);

        return Map.of("isVerified",isVerified, "isExpired",isExpired);
    }


    /**
     * 인증 번호 만료 검증 메서드
     */
    private boolean isCodeValid(EmailVerification emailVerification) {
        LocalDateTime expiredAt = emailVerification.getExpiredAt();
        LocalDateTime now = LocalDateTime.now();
        return now.isBefore(expiredAt);
    }

    /**
     * 인증번호 삭제 메서드
     */
    @Override
    public void deleteCode(String email, int code) {
        emailVerificationRepository.deleteByEmailAndCode(email,code);
    }

    /**
     * 메일 전송 메서드
     */
    private void sendEmailVerification(String email) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject("나만의 냉장고 회원가입 인증번호 안내."); // 이메일 제목 설정
        message.setText(getText()); // 이메일 내용 설정
        message.setFrom(emailFrom); // 발신자 이메일 주소 설정
        message.setTo(email); // 수신자 이메일 주소 설정
        mailSender.send(message); // 이메일 발송
    }


    /**
     * 메일 내용 메서드
     */
    private String getText(){
        StringBuffer buffer = new StringBuffer();
        buffer.append("회원 가입을 진행하기 위해서 아래절차를 따라해주세요. "); // 회원 가입 안내 메시지 추가
        buffer.append("아래의 코드를 인증번호 칸에 입력해주세요. "); // 회원 가입 안내 메시지 추가
        buffer.append(code);
        buffer.append(System.lineSeparator()).append(System.lineSeparator()); // 공백 라인 추가
        buffer.append("Regards,").append(System.lineSeparator()).append("나만의 냉장고"); // 문서 마무리 부분 추가
        return buffer.toString(); // 완성된 이메일 내용 반환
    }
}
