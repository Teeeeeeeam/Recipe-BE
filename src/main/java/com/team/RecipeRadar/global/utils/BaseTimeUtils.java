package com.team.RecipeRadar.global.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeUtils {

    //저장될 때 시간이 자동 저장
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @LastModifiedDate
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime updatedAt;
}
