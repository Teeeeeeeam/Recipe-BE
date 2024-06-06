package com.team.RecipeRadar.domain.notification.dao;

import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.member.domain.Member;
import com.team.RecipeRadar.domain.notification.domain.Notification;
import com.team.RecipeRadar.domain.notification.dto.NotificationDto;
import com.team.RecipeRadar.domain.post.dao.PostRepository;
import com.team.RecipeRadar.domain.post.domain.Post;
import com.team.RecipeRadar.global.config.querydsl.QueryDslConfig;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
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

import static com.team.RecipeRadar.domain.notification.domain.NotificationType.*;
import static org.assertj.core.api.Assertions.*;

@Slf4j
@DataJpaTest
@Import(QueryDslConfig.class)
//@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
@Transactional
@ActiveProfiles("test")
class NotificationRepositoryTest {

    @Autowired NotificationRepository notificationRepository;
    @Autowired MemberRepository memberRepository;
    @Autowired PostRepository postRepository;
    @Autowired CommentRepository commentRepository;

    private final String POST_URL ="/api/user/posts/";

    @Test
    @DisplayName("알림기능 무한 페이징 기능")
    void slice_Page(){
        List<Notification> notificationList = new ArrayList<>();
        Member member = Member.builder().id(2l).nickName("작성자").build();

        Member save_member = memberRepository.save(member);
        for(int i = 0 ;i<10;i++) {
            notificationList.add(Notification.builder().notificationType(POSTLIKE).toName("작성자").url(POST_URL + 2).receiver(save_member).build());
        }
        List<Notification> notificationList1 = notificationRepository.saveAll(notificationList);
        Pageable pageable = PageRequest.of(0, 2);
        Slice<NotificationDto> notificationDtos = notificationRepository.notificationPage(notificationList1.get(0).getReceiver().getId(), pageable, null);

        List<NotificationDto> content = notificationDtos.getContent();

        assertThat(content).hasSize(2);
        assertThat(notificationDtos.hasNext()).isTrue();
    }
    
    @Test
    @DisplayName("메인 페이지 7개만 나오기")
    void mainPage(){
        Long memberId = 2l;
        Member member = Member.builder().id(memberId).nickName("작성자").build();
        Member save_member = memberRepository.save(member);

        List<Notification> notificationList = new ArrayList<>();
        for(int i = 0 ;i<10;i++) {
            notificationList.add(Notification.builder().notificationType(POSTLIKE).toName("작성자").url(POST_URL + 2).receiver(save_member).build());
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
    void deleteComment() {
        Member member = Member.builder().nickName("작성자").build();
        Member save_member = memberRepository.save(member);

        Member to = Member.builder().nickName("대글단자").build();
        Member toMember = memberRepository.save(to);

        Post post = Post.builder().postTitle("제목").build();
        Post save = postRepository.save(post);
        Comment comment = Comment.builder().id(1L).post(save).commentContent("댓글 내용").member(save_member).build();
        commentRepository.save(comment);

        Notification notification = Notification.builder()
                .notificationType(COMMENT)
                .toName(toMember.getNickName())
                .url(POST_URL + 1)
                .receiver(save_member)
                .build();
        notificationRepository.save(notification);

        notificationRepository.deleteComment(toMember.getId(), member.getId(), comment.getId());

        List<Notification> notifications = notificationRepository.findAll();
        assertThat(notifications).isEmpty();
    }
}