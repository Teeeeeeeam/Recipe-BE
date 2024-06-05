package com.team.RecipeRadar.domain.notification.domain;

import com.team.RecipeRadar.domain.member.domain.Member;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@ToString(exclude = "receiver") // receiver 필드를 toString()에서 제외
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private NotificationContent content;

    @Embedded
    private RelatedUrl url;

    private Boolean isRead;     // 조회 여부

    private String toName;

    @Enumerated(EnumType.STRING)
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
        this.isRead =false;
    }

    public String getContent() {
        return content.getContent();
    }

    public String getUrl() {
        return url.getUrl();
    }

    public void read(){
        isRead = true;
    }

}
