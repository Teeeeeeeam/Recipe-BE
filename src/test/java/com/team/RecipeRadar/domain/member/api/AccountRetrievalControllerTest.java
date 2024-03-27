package com.team.RecipeRadar.domain.member.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.team.RecipeRadar.domain.member.application.AccountRetrievalService;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.global.jwt.utils.JwtProvider;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Handler;
import com.team.RecipeRadar.global.security.oauth2.CustomOauth2Service;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountRetrievalController.class)
@ExtendWith(SpringExtension.class)
@Slf4j
class AccountRetrievalControllerTest {

    @MockBean
    private AccountRetrievalService accountRetrievalService;
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    MemberRepository memberRepository;
    @MockBean
    JwtProvider jwtProvider;
    @MockBean
    CustomOauth2Handler customOauth2Handler;
    @MockBean
    CustomOauth2Service customOauth2Service;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @DisplayName("아이디 찾기 컨트롤러 테스트")
    void test() throws Exception{

        String username = "test";
        String email="test@email.com";
        String code = "code";

        MemberDto build = MemberDto.builder().email(email).username(username).build();

        List<Map<String, String>> mapList = new ArrayList<>();
        Map<String, String> map = new LinkedHashMap<>();
        map.put("로그인 타입","normal");
        map.put("로그인 타입","keuye0638");
        mapList.add(map);

        given(accountRetrievalService.findLoginId(eq(username),eq(email),eq(code))).willReturn(mapList);

        mockMvc.perform(get("/api/loginid/find")
                .param("code",code)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(build)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0]['로그인 타입']").value("keuye0638"));
    }
}