package com.gamzabat.algohub.feature.studygroup.dto;

import lombok.Getter;

import java.time.LocalDate;

@Getter
public class GetGroupMemberResponse {

    private String nickname;
    private LocalDate joinDate;
    private String achivement;
    private Boolean isOwner;

    public GetGroupMemberResponse(String nickname, LocalDate joinDate, String achivement, Boolean isOwner) {
        this.nickname = nickname;
        this.joinDate = joinDate;
        this.achivement = achivement;
        this.isOwner = isOwner;
    }
}
