package com.team.RecipeRadar.global.auth.domain;

import com.team.RecipeRadar.domain.member.domain.Member;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = "member")
public class RefreshToken {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 500)
    private String refreshToken;

    @OneToOne
    @JoinColumn(name = "member_id")
    private Member member;

    private LocalDateTime tokenTIme;

    public void update(String refreshToken){
        this.refreshToken = refreshToken;
    }
    public static RefreshToken createRefreshToken(Member member,String refreshToken,LocalDateTime expired){
        return  RefreshToken.builder().member(member).refreshToken(refreshToken).tokenTIme(expired).build();
    }
}
