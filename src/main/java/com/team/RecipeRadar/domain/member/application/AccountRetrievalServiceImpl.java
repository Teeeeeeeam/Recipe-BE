package com.team.RecipeRadar.domain.member.application;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.global.email.application.JoinEmailServiceImplV1;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
public class AccountRetrievalServiceImpl implements AccountRetrievalService{

    private final MemberRepository memberRepository;
    private final JoinEmailServiceImplV1 joinEmailServiceImplV1;

public List<Map<String ,String>> findLoginId(String username, String email, String code) {
    List<Member> byUsernameAndEmail = memberRepository.findByUsernameAndEmail(username, email);
    log.info("bbb={}",byUsernameAndEmail);

    List<Map<String,String>> list = new LinkedList<>();

    Boolean emailCode = emailCode(code);

    Map<String, String> errorMap = new LinkedHashMap<>();

    if (emailCode) {
        if (byUsernameAndEmail.isEmpty()) {
            errorMap.put("가입 정보", "해당 정보로 가입된 회원은 없습니다.");
            list.add(errorMap);
        } else{
            for (Member member : byUsernameAndEmail) {
                Map<String, String> loginInfo = new LinkedHashMap<>();
                loginInfo.put("로그인 타입", member.getLogin_type());
                loginInfo.put("로그인 정보", member.getLoginId());
                list.add(loginInfo);
            }
        }
    } else {
        errorMap.put("인증 번호", "인증번호가 일치하지 않습니다.");
        list.add(errorMap);
    }

    return list;
}

    /**
     * 이메일 인증시 인증번호가 유효한지 체크
     * @param code  사용자가 입력한 인증번호
     * @return  일치시 -> true 불일치 false
     */
    public Boolean emailCode(String code){
        String realCode = joinEmailServiceImplV1.getCode();
        if (realCode.equals(code)){
            return true;
        }
        return false;
    }
}
