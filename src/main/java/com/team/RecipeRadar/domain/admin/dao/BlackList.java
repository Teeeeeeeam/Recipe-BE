package com.team.RecipeRadar.domain.admin.dao;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class BlackList {

    @Id@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "black_list_id")
    private Long id;

    private String email;

    private boolean black_check;

    public BlackList(String email, boolean black_check) {
        this.email = email;
        this.black_check = black_check;
    }

    public static BlackList toEntity(String email){
        return BlackList.builder()
                .black_check(true)
                .email(email).build();
    }

    public void unLock(Boolean state){
       if(state) this.black_check= false;
       else this.black_check=true;
    }
}
