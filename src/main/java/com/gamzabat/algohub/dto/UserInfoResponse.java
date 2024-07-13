package com.gamzabat.algohub.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserInfoResponse {
    private String email;

    private String nickname;

    private String profileImage;
}
