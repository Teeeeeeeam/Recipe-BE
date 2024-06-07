package com.team.RecipeRadar.domain.notification.dao;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.team.RecipeRadar.domain.comment.dao.CommentRepository;
import com.team.RecipeRadar.domain.comment.domain.Comment;
import com.team.RecipeRadar.domain.member.dao.MemberRepository;
import com.team.RecipeRadar.domain.notification.domain.Notification;
import com.team.RecipeRadar.domain.notification.domain.NotificationType;
import com.team.RecipeRadar.domain.notification.domain.QNotification;
import com.team.RecipeRadar.domain.notification.dto.NotificationDto;
import com.team.RecipeRadar.domain.questions.domain.QQuestion;
import com.team.RecipeRadar.domain.questions.domain.QuestionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static com.team.RecipeRadar.domain.notification.domain.NotificationType.*;
import static com.team.RecipeRadar.domain.notification.domain.QNotification.*;
import static com.team.RecipeRadar.domain.notification.domain.QNotification.notification;
import static com.team.RecipeRadar.domain.post.domain.QPost.*;
import static com.team.RecipeRadar.domain.questions.domain.QQuestion.*;

@Repository
@Slf4j
@RequiredArgsConstructor
public class CustomNotificationRepositoryImpl implements CustomNotificationRepository{

    private final JPAQueryFactory jpaQueryFactory;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    private final String POST_URL ="/api/user/posts/";
    private final String QUESTION_USER_URL = "/api/user/question/";
    private final String QUESTION_ADMIN_URL = "/api/admin/question/";

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
        log.info("닉네임={}",nickName);
        for(Notification notification : notificationList){
            if(notification.getNotificationType().equals(POSTLIKE)){        //좋아여 타입인지 확인
                Long replace = getReplace(notification.getUrl(), POST_URL);        // post ID 추출
                if(replace == postId && notification.getToName().equals(nickName)){                                  // 넘어온 ID와 추출한 아이디 비교
                        jpaQueryFactory.delete(QNotification.notification).where(QNotification.notification.id.eq(notification.getId())).execute();
                        break;
                }
            }

        }

    }

    /**
     * 알람에서 댓글 삭제
     * @param toId
     * @param fromId
     * @param commentId
     */
    public void deleteComment(Long toId, Long fromId,Long commentId) {    //좋아요 한사람, 게시글 작성자
        List<Notification> notificationList = getNotificationList(fromId);
        String nickName = memberRepository.findById(toId).get().getNickName();

        boolean delete = false;
        for (Notification notification : notificationList) {
            if(delete) break;
            if (notification.getNotificationType().equals(COMMENT)) {
                Long replace_postId = getReplace(notification.getUrl(), POST_URL);        // post ID 추출
                List<Comment> allWithPostId = commentRepository.findAllByPostId(replace_postId);
                for (Comment comment : allWithPostId) {
                    if(comment.getId() == commentId &&  notification.getToName().equals(nickName)) {
//                        boolean existsByReceiverIdAndToName = existsByReceiverIdAndToName(notification.getReceiver().getId(), nickName); // 일치한다면 해당 레코드에서 사용자 비교
//                        if(!existsByReceiverIdAndToName){        //존재했을때 ID획득
//                            id= notification.getId();
//                            break;
//                        }
//                    }
                        jpaQueryFactory.delete(QNotification.notification).where(QNotification.notification.id.eq(notification.getId())).execute();
                        delete= true;
                        break;
                    }
                }
            }
        }

    }

    private List<Notification> getNotificationList(Long fromId) {
        return jpaQueryFactory.select(notification).from(notification).where(notification.receiver.id.eq(fromId)).fetch();
    }


    // 좋아요 해제시 알림 엔티티에서도 삭제하기 위해서 좋아요한 사용자의 id와 게시글 작성자의 id가 존재하는지 확인
    private boolean existsByReceiverIdAndToName(Long memberId, String toName) {
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(notification.receiver.id.eq(memberId).and(notification.toName.eq(toName)));
        log.info("buuasd={}",builder.toString());

        return jpaQueryFactory
                .selectFrom(notification)
                .where(builder)
                .fetchFirst() != null;
    }
    
    // 알림의 표시될 제목획득
    private String getContent(Notification notification){

        String content ="";

        NotificationType notificationType = notification.getNotificationType();

        String toName = notification.getToName();

        if(notificationType.equals(POSTLIKE)){      //좋아요시 URL id 획득
            Long postId = getReplace(notification.getUrl(), POST_URL);
            String postTitle = jpaQueryFactory.select(post.postTitle)
                    .from(post)
                    .where(post.id.eq(postId)).fetchFirst();
            content = toName+"님이 "+postTitle+" 게시글에 좋아요를 했습니다.";
        }
        else if(notificationType.equals(COMMENT)){      // 댓글등록시 id획득
            Long postId = getReplace(notification.getUrl(), POST_URL);
            String postTitle = jpaQueryFactory.select(post.postTitle)
                    .from(post)
                    .where(post.id.eq(postId)).fetchFirst();
            content = toName+"님이 "+ postTitle+" 게시글에 댓글을 달았습니다.";
        }
        else if(notificationType.equals(QUESTION)){
            String roles = notification.getReceiver().getRoles();
            content = null;
            if(roles.equals("ROLE_ADMIN")){
                Long questionId = getReplace(notification.getUrl(), QUESTION_ADMIN_URL);
                QuestionType questionType = jpaQueryFactory.select(question.questionType)
                        .from(question)
                        .where(question.id.eq(questionId)).fetchFirst();
                String body= questionType==QuestionType.ACCOUNT_INQUIRY ?"계정 문의" : "일반 문의";
                content = toName+"님이 "+ body+" 사항을 등록했습니다.";
            }else {
            }
        }
        return content;

    }

    //url에서 postId획득
    private Long getReplace(String url,String replace){
        return Long.parseLong(url.replace(replace,""));
    }
}

