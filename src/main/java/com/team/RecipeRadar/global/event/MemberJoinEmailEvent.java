package com.team.RecipeRadar.global.event;

import com.team.RecipeRadar.domain.member.domain.Member;
import org.springframework.context.ApplicationEvent;

public class MemberJoinEmailEvent extends ApplicationEvent {

    private Member member;

    public MemberJoinEmailEvent(Member member) {
        super(member);
        this.member = member;
    }

    public Member getMember() {
        return member;
    }
}
