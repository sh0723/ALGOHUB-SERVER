package com.gamzabat.algohub.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateRequest {
    String nickname;
    String email;
    String password;

}
