package com.team.RecipeRadar.domain.visit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VisitSession {
    
    @Id
    @Column(name = "visitSession_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String ipAddress;
    private LocalDateTime expiredAt;       //만료 시간

    public static VisitSession toEntity(String ipAddress, LocalDateTime expired_at){
        return VisitSession.builder().ipAddress(ipAddress).expiredAt(expired_at).build();
    }
}
