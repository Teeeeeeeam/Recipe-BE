package com.team.RecipeRadar.global.email.application;

import com.team.RecipeRadar.global.email.domain.EmailVerification;
import com.team.RecipeRadar.global.email.dao.EmailVerificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class JoinEmailServiceImplV2 implements JoinEmailService {

    private final EmailVerificationRepository emailVerificationRepository;

    /**
     * 이메일 검증 토큰을 생성합니다.
     * @param username 유저네임
     * @return 생성된 이메일 검증 토큰
     */
    @Override
    public String generateVerification(String username) {
        if (!emailVerificationRepository.existsByUsername(username)){
            EmailVerification emailVerification = new EmailVerification(username);
            emailVerification = emailVerificationRepository.save(emailVerification);
            return emailVerification.getVerificationId();
        }
        return getVerificationIdByUsername(username);
    }

    /**
     * 유저네임으로 이메일 검증 토큰을 얻습니다.
     * @param username 유저네임
     * @return 이메일 검증 토큰
     */
    @Override
    public String getVerificationIdByUsername(String username) {
        EmailVerification verification = emailVerificationRepository.findByUsername(username);
        if (verification!=null){
            return verification.getVerificationId();
        }
        return null;
    }

    /**
     * 이메일 검증 토큰에 해당하는 유저네임을 얻습니다.
     * @param verificationToken 이메일 검증 토큰
     * @return 해당하는 유저네임
     */
    @Override
    public String getUsernameForVerificationId(String verificationToken) {
        Optional<EmailVerification> emailVerification = Optional.ofNullable(emailVerificationRepository.findById(verificationToken).orElseThrow(() -> new RuntimeException()));
        if (emailVerification.isPresent()){
            return emailVerification.get().getUsername();
        }
        return null;
    }
}
