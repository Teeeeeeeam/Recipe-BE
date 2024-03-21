package com.team.RecipeRadar.domain.member.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    Long id;

    String username;
    String nickName;
    String password;
    String loginId;
    String email;
    @JsonIgnore
    String roles;
    LocalDate join_date;
    String login_type;
    private boolean verified;

    public List<String> getRoleList(){
        if(this.roles != null && this.roles.length() > 0){
            return Arrays.asList(this.roles.split(","));
        }
        return new ArrayList<>();
    }

}
