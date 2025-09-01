package com.lean2708.auth_service.dto.request;

import lombok.Getter;

@Getter
public class SetPasswordRequest {

    private String email;
    private String password;
    private String confirmPassword;
}
