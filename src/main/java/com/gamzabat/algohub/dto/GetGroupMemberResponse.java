package com.gamzabat.algohub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class GetGroupMemberResponse {

    private Long id;
    private String nickname;
    private String profileImage;

    public GetGroupMemberResponse(String nickname, String profileImage, Long id) {
        this.nickname = nickname;
        this.profileImage = profileImage;
        this.id = id;
    }

}
