package com.team.RecipeRadar.domain.blackList.dao;

import com.team.RecipeRadar.domain.blackList.domain.BlackList;
import com.team.RecipeRadar.domain.blackList.dto.BlackListDto;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@Import(QueryDslConfig.class)
@DataJpaTest
@ActiveProfiles("test")
class BlackListRepositoryTest {

    @Autowired BlackListRepository blackListRepository;


    private List<BlackList> blackLists;
    @BeforeEach
    void setUp(){
        blackLists = List.of(
                BlackList.builder().email("test1@email.com").black_check(false).build(),
                BlackList.builder().email("test1@email.com").black_check(false).build(),
                BlackList.builder().email("test1@email.com").black_check(false).build(),
                BlackList.builder().email("test2@email.com").black_check(false).build(),
                BlackList.builder().email("test3@email.com").black_check(false).build(),
                BlackList.builder().email("test4@email.com").black_check(false).build(),
                BlackList.builder().email("test5@email.com").black_check(false).build());

        blackListRepository.saveAll(blackLists);
    }

    @Test
    @DisplayName("이메일이 존재하는지 확인 테스트(존재시)")
    void existsByEmail(){
        boolean existsByEmail = blackListRepository.existsByEmail("test1@email.com");
        assertThat(existsByEmail).isTrue();
    }

    @Test
    @DisplayName("이메일이 존재하는지 확인 테스트")
    void existsByEmailFail(){
        boolean existsByEmail = blackListRepository.existsByEmail("no`~");
        assertThat(existsByEmail).isFalse();
    }
    
    @Test
    @DisplayName("블랙리스트의 저장된 모든 데이터 조회")
    void getAllBlackList(){
        Slice<BlackListDto> blackListDtos = blackListRepository.allBlackList(null, Pageable.ofSize(3));

        assertThat(blackListDtos.getContent()).hasSize(3);
        assertThat(blackListDtos.hasNext()).isTrue();
    }
    
    @Test
    @DisplayName("블랙리스트의 등록된 이메일을 이메일 검색을 통해 조회")
    void searchEmailBlackList(){
        Slice<BlackListDto> blackListDtos = blackListRepository.searchEmailBlackList("test1@email.com", null, Pageable.ofSize(3));
        assertThat(blackListDtos.getContent()).hasSize(3);
        assertThat(blackListDtos.hasNext()).isFalse();
    }
}