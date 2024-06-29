package com.team.RecipeRadar.domain.notification.dao;

import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.domain.Notification;
import com.team.RecipeRadar.domain.notification.domain.NotificationType;
import com.team.RecipeRadar.domain.notification.dto.NotificationDto;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.config.QueryDslConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.test.context.ActiveProfiles;

import java.util.ArrayList;
import java.util.List;

import static com.team.RecipeRadar.domain.notification.domain.NotificationType.*;
import static org.assertj.core.api.Assertions.*;

@DataJpaTest
@Import(QueryDslConfig.class)
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired NotificationRepository notificationRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;

    private final String POST_URL ="/list-page/user-recipes/";

    private Member member;
    private Pageable pageRequest = PageRequest.of(0,2);

    @BeforeEach
    void setUp(){
        member = Member.builder().id(2l).nickName("작성자").build();
    }
    @Test
    @DisplayName("알림기능 무한 페이징 기능")
    void slice_Page(){
        List<Notification> notificationList = new ArrayList<>();

        Member save_member = memberRepository.save(member);

        for(int i = 0 ;i<10;i++) {
            notificationList.add(Notification.builder().notificationType(POSTLIKE).toName("작성자").url(POST_URL + i).receiver(save_member).build());
        }
        List<Notification> notificationList1 = notificationRepository.saveAll(notificationList);
        Slice<NotificationDto> notificationDtos = notificationRepository.notificationPage(notificationList1.get(0).getReceiver().getId(), pageRequest, null);

        List<NotificationDto> content = notificationDtos.getContent();

        assertThat(content).hasSize(2);
        assertThat(notificationDtos.hasNext()).isTrue();
    }
    
    @Test
    @DisplayName("메인 페이지 7개만 나오기")
    void mainPage(){
        Member saveMember = memberRepository.save(member);

        List<Notification> notificationList = new ArrayList<>();
        for(int i = 0 ;i<10;i++) {
            notificationList.add(Notification.builder().notificationType(POSTLIKE).toName("작성자").url(POST_URL + 2).receiver(saveMember).build());
        }
        List<Notification> notificationList1 = notificationRepository.saveAll(notificationList);
        List<NotificationDto> notificationDtoList = notificationRepository.notificationLimit(notificationList1.get(0).getReceiver().getId());

        assertThat(notificationDtoList).hasSize(7);
    }

    @Test
    @DisplayName("좋아요 해제시 알림 엔티티 삭제")
    void deletePostLike() {

        Member member = Member.builder().nickName("작성자").build();
        Member save_member = memberRepository.save(member);

        Member to = Member.builder().nickName("좋아요한자").build();
        Member toMember = memberRepository.save(to);

        Notification notification = Notification.builder()
                .notificationType(POSTLIKE)
                .toName(toMember.getNickName())
                .url(POST_URL + 1)
                .receiver(save_member)
                .build();
        notificationRepository.save(notification);

        notificationRepository.deletePostLike(toMember.getId(), member.getId(), 1L);

        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).isEmpty();
    }

    @Test
    @DisplayName("댓글 삭제시 알림 엔티티 삭제")
    void deleteCommentNotificationTest() {
        // Given
        Member fromMember = Member.builder().nickName("작성자").build();
        fromMember = memberRepository.save(fromMember);

        Member toMember = Member.builder().nickName("댓글 대상자").build();
        toMember = memberRepository.save(toMember);

        Post post = Post.builder().postTitle("테스트 게시물").build();
        post = postRepository.save(post);

        Comment comment = Comment.builder()
                .id(1L)
                .commentContent("테스트 댓글")
                .member(fromMember)
                .post(post)
                .build();
        comment = commentRepository.save(comment);

        Notification notification = Notification.builder()
                .notificationType(NotificationType.COMMENT)
                .toName(toMember.getNickName())
                .url(POST_URL + post.getId())
                .receiver(fromMember)
                .build();
        notification = notificationRepository.save(notification);

        // When
        notificationRepository.deleteComment(toMember.getId(), fromMember.getId(), comment.getId());

        // Then
        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).isEmpty();
    }
    
    @Test
    @DisplayName("사용자 탈퇴시 사용자관련 엔티티 모두삭제")
    void deleteNotification(){
        Member save = memberRepository.save(member);

        List<Notification> notifications = List.of(
                Notification.builder().receiver(save).notificationType(POSTLIKE).build(),
                Notification.builder().receiver(save).notificationType(COMMENT).build()
        );
        notificationRepository.saveAll(notifications);
        List<Notification> before = notificationRepository.findAll();
        notificationRepository.deleteMember(save.getId());

        List<Notification> after = notificationRepository.findAll();

        assertThat(before).isNotEmpty();
        assertThat(after).isEmpty();

    }
}