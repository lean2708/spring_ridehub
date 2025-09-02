package com.lean2708.auth_service.dto.response;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class TokenResponse {

    private String accessToken;
    private String refreshToken;
    private boolean authenticated;
    private String email;

//    private Set<EntityBasic> roles;

}
