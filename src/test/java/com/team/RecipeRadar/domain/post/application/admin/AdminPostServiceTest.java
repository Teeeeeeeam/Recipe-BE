package com.team.RecipeRadar.domain.post.application.admin;

import com.team.RecipeRadar.domain.balckLIst.dto.response.PostsCommentResponse;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.SliceImpl;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
class AdminPostServiceTest {

    @Mock PostRepository postRepository;
    @Mock CommentRepository commentRepository;

    @InjectMocks
    AdminPostServiceImpl adminService;

    @Test
    @DisplayName("전체 게시글 조회")
    void count_posts(){
        long count =550;

        when(postRepository.countAllBy()).thenReturn(count);

        long l = adminService.searchAllPosts();
        assertThat(l).isEqualTo(count);
    }

    @Test
    @DisplayName("게시글 관련 댓글 페이징 변환 테스트")
    void postsContainsComment(){
        Long post_id= 1l;

        PageRequest pageRequest = PageRequest.of(0, 2);

        List<CommentDto> commentDtoList = List.of(
                CommentDto.builder().commentContent("댓글1").member(MemberDto.builder().loginId("testId").nickname("닉네임1").build()).build(),
                CommentDto.builder().commentContent("댓글2").member(MemberDto.builder().loginId("testId1").nickname("닉네임2").build()).build()
        );
        SliceImpl<CommentDto> commentDtos = new SliceImpl<>(commentDtoList, pageRequest, false);

        when(commentRepository.getPostComment(eq(post_id),isNull(),eq(pageRequest))).thenReturn(commentDtos);

        PostsCommentResponse postsComments = adminService.getPostsComments(post_id, null, pageRequest);

        assertThat(postsComments.getComment()).hasSize(2);
        assertThat(postsComments.getComment().get(0).getCommentContent()).isEqualTo("댓글1");
        assertThat(postsComments.getNextPage()).isFalse();
    }

}