package com.team.RecipeRadar.event;

import com.team.RecipeRadar.Entity.Member;
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
