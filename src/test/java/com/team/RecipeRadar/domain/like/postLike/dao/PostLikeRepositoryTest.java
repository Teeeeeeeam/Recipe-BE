package com.team.RecipeRadar.domain.like.postLike.dao;

import com.team.RecipeRadar.domain.like.postLike.domain.PostLike;
import com.team.RecipeRadar.domain.like.postLike.dto.PostLikeDto;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.dto.PostDto;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

import java.util.*;

import static org.assertj.core.api.Assertions.*;


@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
@Slf4j
class PostLikeRepositoryTest {

    @Autowired MemberRepository memberRepository;
    @Autowired PostLikeRepository postLikeRepository;
    @Autowired PostRepository postRepository;

    ArrayList<PostLike> ary = new ArrayList();

    @BeforeEach
    void init(){
        for (int i =0;i<10;i++) {
            Member member = Member.builder().loginId("testId"+i).build();
            Post post = Post.builder().postContent(i+" 번째 게시글").postTitle("타이틀").postServing("서블").postCookingTime("이름").postCookingLevel("레벨").build();

            Member saveMember = memberRepository.save(member);
            Post savePost = postRepository.save(post);

            PostLike postLike = PostLike.builder().post(savePost).member(saveMember).build();
            PostLike like = postLikeRepository.save(postLike);
            ary.add(like);
        }

        Optional<Post> byId = postRepository.findById(2l);
        Member member = memberRepository.findById(4l).get();
        PostLike postLike = PostLike.builder().post(byId.get()).member(member).build();
        postLikeRepository.save(postLike);
    }

    @Test
    @DisplayName("게시글 좋아요 엔티티 저장")
    void save(){
        for (int i =0;i<10;i++) {
            PostLike postLike = ary.get(i);
            PostLike like = postLikeRepository.findById(postLike.getId()).get(); // 저장된 PostLike 엔티티의 ID 값 사용
            assertThat(like).isInstanceOf(PostLike.class);
            assertThat(like.getPost().getPostContent()).isEqualTo(i+" 번째 게시글");
            assertThat(like.getMember().getLoginId()).isEqualTo("testId"+i);
        }

        Boolean aBoolean = postLikeRepository.existsByMemberIdAndPostId(1l, 1l);
        assertThat(aBoolean).isTrue();

        Boolean aBoolean1 = postLikeRepository.existsByMemberIdAndPostId(1l, 2l);
        assertThat(aBoolean1).isFalse();
    }

    
    @Test
    @DisplayName("좋아여 삭제 테스트")
    void delete_postLikes(){

        postLikeRepository.deleteByMemberIdAndPostId(1l,1l); // 일차하는 필드값 삭제
        postLikeRepository.deleteByMemberIdAndPostId(2l,7l); //일치하지 않는 값 삭제

        Optional<PostLike> byId = postLikeRepository.findById(1l);
        assertThat(byId).isEmpty();

        Optional<PostLike> byId1 = postLikeRepository.findById(2l);
        assertThat(byId1).isNotEmpty();
    }

}