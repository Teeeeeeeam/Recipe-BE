package com.team.RecipeRadar.domain.notification.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.notification.domain.Notification;
import com.team.RecipeRadar.domain.notification.domain.NotificationType;
import com.team.RecipeRadar.domain.notification.domain.QNotification;
import com.team.RecipeRadar.domain.notification.dto.NotificationDto;
import com.team.RecipeRadar.domain.qna.domain.QuestionType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.member.domain.QMember.*;
import static com.team.RecipeRadar.domain.notification.domain.NotificationType.*;
import static com.team.RecipeRadar.domain.notification.domain.QNotification.notification;
import static com.team.RecipeRadar.domain.post.domain.QPost.*;
import static com.team.RecipeRadar.domain.qna.domain.QQuestion.*;

@Repository
@RequiredArgsConstructor
public class CustomNotificationRepositoryImpl implements CustomNotificationRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    private final String POST_URL ="/list-page/user-recipes/";
    private final String QUESTION_USER_URL = "/my-page/success/answer/";
    private final String QUESTION_ADMIN_URL = "/admin/questions/";

    @Override
    public Slice<NotificationDto> notificationPage(Long memberId, Pageable pageable, Long lastId) {
        BooleanBuilder builder = new BooleanBuilder();
        if(lastId!=null){
            builder.and(notification.id.lt(lastId));
        }

        List<Notification> fetch = jpaQueryFactory.select(notification)
                .from(notification)
                .where(builder, notification.receiver.id.eq(memberId))
                .orderBy(notification.id.desc())
                .limit(pageable.getPageSize() + 1)
                .fetch();

        List<NotificationDto> notificationDtoList = fetch.stream().map(
                notification -> new NotificationDto(notification.getId(), getContent(notification), notification.getUrl())
        ).collect(Collectors.toList());

        boolean next = false;

        if (notificationDtoList.size()>pageable.getPageSize()) {
            notificationDtoList.remove(pageable.getPageSize());
            next = true;
        }
        return new SliceImpl<>(notificationDtoList,pageable,next);

    }

    @Override
    public List<NotificationDto> notificationLimit(Long memberId) {
        List<Notification> fetch = jpaQueryFactory.select(notification)
                .from(notification)
                .where(notification.receiver.id.eq(memberId))
                .orderBy(notification.id.desc())
                .limit(7).fetch();

        List<NotificationDto> notificationDtoList = fetch.stream()
                .map(notification -> NotificationDto.of(notification.getId(), getContent(notification), notification.getUrl()))
                .collect(Collectors.toList());

        return notificationDtoList;
    }

    /**
     * 게시글 좋아여 해제시 알림 에서도 내역을 삭제
     * @param toId 좋아요한 사용자
     * @param fromId    게시글 작성자
     * @param postId    현재 게시글 ID
     */
    @Override
    public void deletePostLike(Long toId, Long fromId,Long postId) {    //좋아요 한사람, 게시글 작성자
        List<Notification> notificationList = getNotificationList(fromId);
        String nickName = memberRepository.findById(toId).get().getNickName();

        notificationList.stream()
                .filter(notification -> notification.getNotificationType().equals(POSTLIKE)) // 좋아요 타입인지 확인
                .filter(notification-> {
                    Long replace = getReplace(notification.getUrl(), POST_URL);  // post ID 추출
                    return replace.equals(postId) && notification.getToName().equals(nickName);      // 넘어온 ID와 추출한 아이디 비교
                })
                .findFirst()//첫 번째 알림
                .ifPresent(
                        notification -> jpaQueryFactory.delete(QNotification.notification).
                                where(QNotification.notification.id.eq(notification.getId())).execute()
                );
    }

    /**
     * 알람에서 댓글 삭제
     */
    public void deleteComment(Long toId, Long fromId,Long commentId) {    //좋아요 한사람, 게시글 작성자
        List<Notification> notificationList = getNotificationList(fromId);
        String nickName = memberRepository.findById(toId).get().getNickName();

        notificationList.stream()
                .filter( notification ->  notification.getNotificationType().equals(COMMENT))   //댓글인지 확인
                .filter(notification -> {
                    Long replace = getReplace(notification.getUrl(), POST_URL); // post ID 추출
                     return commentRepository.findAllByPostId(replace).stream()
                            .anyMatch(comment -> comment.getId() == commentId && notification.getToName().equals(nickName)); // 조건 일치 확인
                })
                .findFirst()
                .ifPresent(notification -> {
                    deleteCommentNotification(notification);
                });
    }

    private void deleteCommentNotification(Notification notification) {
        jpaQueryFactory.delete(QNotification.notification).where(QNotification.notification.id.eq(notification.getId())).execute();
    }

    /**
     * 사용자 탈퇴사 관련된 알림 객체 삭제
     */
    @Override
    public void deleteMember(Long memberId) {
        jpaQueryFactory.delete(notification)
                .where(notification.receiver.id.in(
                        JPAExpressions.select(member.id)
                                .from(member).where(member.id.eq(memberId))
                )).execute();
    }

    private List<Notification> getNotificationList(Long fromId) {
        return jpaQueryFactory.select(notification).from(notification).where(notification.receiver.id.eq(fromId)).fetch();
    }

    // 알림의 표시될 제목획득
    private String getContent(Notification notification) {
        String content = "";
        NotificationType notificationType = notification.getNotificationType();
        String toName = notification.getToName();

        switch (notificationType) {
            case POSTLIKE:
                content = getPostLikeContent(notification, toName);
                break;
            case COMMENT:
                content = getCommentContent(notification, toName);
                break;
            case QUESTION:
                content = getQuestionContent(notification, toName);
                break;
            default:
                break;
        }

        return content;
    }

    /* 게시글 좋아요 알림 내용 */
    private String getPostLikeContent(Notification notification, String toName) {
        Long postId = getReplace(notification.getUrl(), POST_URL);
        String postTitle = jpaQueryFactory.select(post.postTitle)
                .from(post)
                .where(post.id.eq(postId)).fetchFirst();
        return toName + "님이 " + postTitle + " 게시글에 좋아요를 했습니다.";
    }

    /* 댓글 작성 알림 내용 */
    private String getCommentContent(Notification notification, String toName) {
        Long postId = getReplace(notification.getUrl(), POST_URL);
        String postTitle = jpaQueryFactory.select(post.postTitle)
                .from(post)
                .where(post.id.eq(postId)).fetchFirst();
        return toName + "님이 " + postTitle + " 게시글에 댓글을 달았습니다.";
    }

    /* 질문 알림 내용 */
    private String getQuestionContent(Notification notification, String toName) {
        String content = "";
        String roles = notification.getReceiver().getRoles();

        if ("ROLE_ADMIN".equals(roles)) {           // 어드민 사용자일떄 가는 알림
            Long questionId = getReplace(notification.getUrl(), QUESTION_ADMIN_URL);
            QuestionType questionType = jpaQueryFactory.select(question.questionType)
                    .from(question)
                    .where(question.id.eq(questionId)).fetchFirst();
            String body = (questionType == QuestionType.ACCOUNT_INQUIRY) ? "계정 문의" : "일반 문의";
            content = toName + "님이 " + body + " 사항을 등록했습니다.";
        } else {                                    // 일반 사용자에게 답변이 작성되었을때
            Long replaceUrl = getReplace(notification.getUrl(), QUESTION_USER_URL);
            String questionTitle = jpaQueryFactory.select(question.title)
                    .from(question)
                    .where(question.id.eq(replaceUrl)).fetchFirst();
            content = toName + "님이 " + questionTitle + " 의 답변이 작성되었습니다.";
        }

        return content;
    }
    /* 저장된 url에서 Id값 획득 */
    private Long getReplace(String url,String replace){
        return Long.parseLong(url.replace(replace,""));
    }
}

