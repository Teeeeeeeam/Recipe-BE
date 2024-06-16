package com.team.RecipeRadar.domain.comment.application;

import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.comment.dto.user.UserDeleteCommentRequest;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.application.NotificationService;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.domain.post.dto.PostDto;
import com.team.RecipeRadar.domain.comment.dto.CommentDto;
import com.team.RecipeRadar.global.exception.ex.NoSuchDataException;
import com.team.RecipeRadar.global.exception.ex.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@Slf4j
class CommentServiceImplTest {

    @Mock MemberRepository memberRepository;
    @Mock PostRepository postRepository;
    @Mock CommentRepository commentRepository;
    @Mock NotificationService notificationService;

    @InjectMocks CommentServiceImpl commentService;

    private final Long memberId = 1l;
    private final Long postId = 1l;


    @Test
    @DisplayName("Comment_save 저장테스트")
    public void testSave_ValidMemberAndArticle_ReturnsSavedComment() {
        Member member = getMember();
        Post post = getPost(member);
        Comment comment = getComment(member, post);

        when(memberRepository.findById(eq(memberId))).thenReturn(Optional.of(member));
        when(postRepository.findById(eq(postId))).thenReturn(Optional.of(post));

        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        commentService.save(postId, "내용!!", memberId);

        verify(commentRepository, times(1)).save(any(Comment.class)); // Verify save method was called exactly once

    }

    @Test
    @DisplayName("NoDataElementException 오류테스트")
    public void testSave_InvalidMemberOrArticle_ThrowsNoSuchElementException() {

        assertThatThrownBy(() -> commentService.save(1l,"변경오류",1l))
                .isInstanceOf(NoSuchDataException.class);
    }

    @Test
    @DisplayName("댓글 삭제 검증")
    void delete_comment_Test() {
        Member member = getMember();
        Post post = getPost(member);
        Comment comment = getComment(member, post);


        UserDeleteCommentRequest userDeleteCommentRequest = new UserDeleteCommentRequest();
        userDeleteCommentRequest.setCommentId(1l);

        // Member 리포지토리 mock 설정
        when(memberRepository.findById(anyLong())).thenReturn(Optional.of(member));

        // Comment 리포지토리 mock 설정
        when(commentRepository.findById(anyLong())).thenReturn(Optional.of(comment));

        doNothing().when(notificationService).deleteCommentNotification(anyLong(),anyLong(),anyLong());
        // 테스트 수행
        commentService.deleteComment(1l,memberId);

        //deleteMemberId 메소드가 한번 실행이 됬는지 확인하는
        verify(commentRepository, times(1)).deleteMemberId(member.getId(), comment.getId());

    }
    @Test
    @DisplayName("비 작성자가 댓글을 삭제하려고 하는 경우")
    void delete_comment_by_non_author_Test() {
        Member member = Member.builder().id(1l).roles("ROLE_USER").build(); //작성자
        Member member_fail = Member.builder().id(2l).roles("ROLE_USER").build(); //비 작성자
        Comment comment = Comment.builder().id(2l).commentContent("댓글 수정전").member(member).build();

        when(memberRepository.findById(member_fail.getId())).thenReturn(Optional.of(member_fail));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));


        assertThatThrownBy(() -> commentService.deleteComment(2l,2l))
                .isInstanceOf(UnauthorizedException.class);

    }
    @Test
    @DisplayName("댓글 모두조회 페이징 처리 테스트")
    void page_comment_test(){
        //게시글 아이디
        long postId = Long.parseLong("55");

        String nickName = "testNickName";
        PostDto articleDto = PostDto.builder().id(postId).build();

        // 페이징 테스트를 위한 객체 생성
        List<CommentDto> commentDtos = new ArrayList<>();

        //더미 데이터 10개 생성
        for (int i = 1; i <= 10; i++) {
            CommentDto commentDto = CommentDto.builder()
                    .id((long) i)
                    .commentContent("테스트 댓글 내용 " + i)
                    .nickName(nickName)
                    .articleDto(articleDto)
                    .creatAt(LocalDateTime.now())
                    .build();
            commentDtos.add(commentDto);
        }

        Pageable pageable = PageRequest.of(0, 5);

        List<Comment> comments = new ArrayList<>();

        for (int i = 1; i <= 10; i++) {
            Comment comment = Comment.builder()
                    .id((long) i)
                    .commentContent("테스트 댓글 내용 " + i)
                    .member(Member.builder().id(1L).build())
                    .post(Post.builder().id(postId).build())
                    .build();
            comments.add(comment);
        }

        PageImpl<Comment> commentPage = new PageImpl<>(comments);

        //Comment 객체의 데이터 반환
        when(commentRepository.findAllByPost_Id(postId,pageable)).thenReturn(commentPage);

        //테스트 실행
        Page<CommentDto> result = commentService.commentPage(postId, pageable);
        assertThat(result.getTotalElements()).isEqualTo(10);
        assertThat(result.getTotalPages()).isEqualTo(1);        //jpa는 0부터 1페이지 2개시 ->(0,2) 두개
        for (int i = 0; i < 10; i++) {
            assertThat(result.getContent().get(i).getCommentContent()).isEqualTo("테스트 댓글 내용 " + (i + 1));
        }
        assertThat(result.getContent().size()).isEqualTo(10);
        assertThat(result.getNumber()).isEqualTo(0);
    }

    @Test
    @DisplayName("댓글 작성자가 수정 톄스트")
    void update_comment(){
        String update_success="댓글 수정 성공!!";

        Member member = Member.builder().id(1l).build();
        Comment comment = Comment.builder().id(2l).commentContent("댓글 수정전").member(member).build();

        when(memberRepository.findById(member.getId())).thenReturn(Optional.of(member));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        commentService.update(2l,update_success,1l);

        assertThat(comment.getCommentContent()).isEqualTo(update_success);
        assertThat(comment.getCommentContent()).isNotEqualTo("댓글 수정!");
        assertThat(comment.getMember().getId()).isEqualTo(member.getId());
    }

    @Test
    @DisplayName("댓글 비작성자가 수정시 오류발생 톄스트")
    void update_comment_throws(){

        Member member = Member.builder().id(1l).roles("ROLE_USER").build(); //작성자
        Member member_fail = Member.builder().id(2l).roles("ROLE_USER").build(); //비 작성자
        Comment comment = Comment.builder().id(2l).commentContent("댓글 수정전").member(member).build();

        when(memberRepository.findById(member_fail.getId())).thenReturn(Optional.of(member_fail));
        when(commentRepository.findById(comment.getId())).thenReturn(Optional.of(comment));

        assertThatThrownBy(() -> commentService.update(2l,"수정!",2l)).isInstanceOf(UnauthorizedException.class);
    }

    private static Comment getComment(Member member, Post post) {
        return Comment.builder().id(3l).commentContent("댓글").member(member).post(post).build();
    }

    private Post getPost(Member member) {
        return Post.builder().member(member).id(postId).postTitle("제목").build();
    }

    private Member getMember() {
        return Member.builder().id(memberId).nickName("닉네임").roles("ROLE_USER").build();

    }
}