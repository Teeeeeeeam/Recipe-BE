package com.team.RecipeRadar.domain.post.dao;

import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(QueryDslConfig.class)
@Transactional
@ActiveProfiles("test")
@Slf4j
class PostRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;


    @Test
    @DisplayName("무한 페이징 테스트 진행")
    void pagingTest(){
        Pageable request = PageRequest.of(0, 2);

        List<Post> postList = new ArrayList<>();
        Member member = Member.builder().nickName("닉네임 1").build();
        Member member1 = Member.builder().nickName("닉네임 2").build();
        Member save = memberRepository.save(member);
        Member save1 = memberRepository.save(member1);

        postList.add(Post.builder().postTitle("tit").postContent("컨텐트1").postCookingLevel("coo").member(save).postCookingTime("ti").build());
        postList.add(Post.builder().postTitle("tit").postContent("컨텐트2").postCookingLevel("coo").member(save1).postCookingTime("ti").build());
        postList.add(Post.builder().postTitle("tit").postContent("컨텐트3").postCookingLevel("coo").member(save).postCookingTime("ti").build());
        postList.add(Post.builder().postTitle("tit").postContent("컨텐트4").postCookingLevel("coo").member(save).postCookingTime("ti").build());

        postRepository.saveAll(postList);

        Slice<PostDto> allPost = postRepository.getAllPost(request);

        assertThat(allPost.hasNext()).isTrue();
        assertThat(allPost.getContent()).hasSize(2);
    }

}