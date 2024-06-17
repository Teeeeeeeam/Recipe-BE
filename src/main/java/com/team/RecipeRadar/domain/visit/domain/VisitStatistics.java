package com.team.RecipeRadar.domain.visit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VisitStatistics {

    @Id
    @Column(name = "visitStatistics_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private Integer visitCount;  // 방문 횟수
    private LocalDateTime day;   // 일자

}
