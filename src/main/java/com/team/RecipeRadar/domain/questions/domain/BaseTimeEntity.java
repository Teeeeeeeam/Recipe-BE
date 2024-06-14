package com.team.RecipeRadar.domain.questions.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.time.LocalDateTime;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class BaseTimeEntity {

    //저장될 때 시간이 자동 저장
    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private LocalDateTime modifiedDate;
}
