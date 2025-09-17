package com.lean2708.profile_service.dto.request;

import lombok.Getter;

@Getter
public class UserProfileRequest {

    private Long userId;
    private String name;
    private String phone;
    private String email;

}
