package com.lean2708.auth_service.dto.request;

import lombok.Getter;

@Getter
public class RegisterDetailsRequest {

    private String email;
    private String name;
    private String phone;

}
