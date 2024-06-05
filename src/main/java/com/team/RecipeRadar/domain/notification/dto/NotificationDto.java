package com.team.RecipeRadar.domain.notification.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.team.RecipeRadar.domain.member.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class NotificationDto {

    private Long id;
    private String content;
    private MemberDto memberDto;
    private String url;


    public NotificationDto(Long id, String content,String url) {
        this.id = id;
        this.content = content;
        this.url = url;
    }

    public static NotificationDto of(Long id, String content,String url){
        return new NotificationDto(id,content,url);
    }
}
