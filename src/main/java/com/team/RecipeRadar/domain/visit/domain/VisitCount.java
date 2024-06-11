package com.team.RecipeRadar.domain.visit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class VisitCount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String ipAddress;
    private LocalDateTime expired_at;       //만료 시간

    public static VisitCount toEntity(String ipAddress, LocalDateTime expired_at){
        return VisitCount.builder().ipAddress(ipAddress).expired_at(expired_at).build();
    }
}
