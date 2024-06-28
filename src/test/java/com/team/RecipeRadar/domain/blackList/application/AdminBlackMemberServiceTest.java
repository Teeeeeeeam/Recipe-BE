package com.team.RecipeRadar.domain.blackList.application;

import com.team.RecipeRadar.domain.blackList.dao.BlackListRepository;
import com.team.RecipeRadar.domain.blackList.domain.BlackList;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminBlackMemberServiceTest {

    @Mock BlackListRepository blackListRepository;
    @InjectMocks AdminBlackMemberServiceImpl adminService;

    @Test
    @DisplayName("이메일 차단 유뮤 테스트")
    void temporarilyUnblockUser(){
        BlackList blackList = BlackList.builder().id(1l).black_check(false).email("test@example.com").build();

        when(blackListRepository.findById(eq(1l))).thenReturn(Optional.of(blackList));
        when(blackListRepository.save(any(BlackList.class))).thenReturn(blackList);

        boolean temporarilied = adminService.temporarilyUnblockUser(1l);

        assertThat(temporarilied).isTrue();
    }
}