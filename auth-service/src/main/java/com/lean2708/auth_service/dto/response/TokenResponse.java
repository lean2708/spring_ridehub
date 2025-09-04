package com.lean2708.auth_service.dto.response;
import com.lean2708.auth_service.dto.basic.EntityBasic;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Builder
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private boolean authenticated;
    private String email;

    private Set<EntityBasic> roles;

}
