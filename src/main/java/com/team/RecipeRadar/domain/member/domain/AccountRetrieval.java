package com.team.RecipeRadar.domain.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountRetrieval {

    @Id
    @GeneratedValue(generator = "UUID_GENERATOR")
    @GenericGenerator(name = "UUID_GENERATOR", strategy = "org.hibernate.id.UUIDGenerator")
    private String verificationId;

    private String loginId;

}
