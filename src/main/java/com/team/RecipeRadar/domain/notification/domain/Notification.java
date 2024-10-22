package com.team.RecipeRadar.domain.notification.domain;

import com.team.RecipeRadar.domain.member.domain.Member;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@Table(indexes = {
        @Index(columnList = "notification_type"),
        @Index(columnList = "member_id"),
        @Index(columnList = "url"),
})
@Getter
@NoArgsConstructor
@ToString(exclude = "receiver")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private NotificationContent content;

    @Embedded
    private RelatedUrl url;

    private String toName;

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type")
    private NotificationType notificationType;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member receiver;

    @Builder
    public Notification(Member receiver, NotificationType notificationType, String content,String url,String toName){
        this.receiver = receiver;
        this.notificationType = notificationType;
        this.toName = toName;
        this.content = new NotificationContent(content);
        this.url=  new RelatedUrl(url);
    }

    public String getContent() {
        return content.getContent();
    }

    public String getUrl() {
        return url.getUrl();
    }

}
