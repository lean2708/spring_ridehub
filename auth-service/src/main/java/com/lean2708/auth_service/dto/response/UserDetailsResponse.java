package com.lean2708.auth_service.dto.response;

import com.lean2708.auth_service.constants.RegistrationStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDetailsResponse {

    private String email;
    private String name;
    private String phone;
    private RegistrationStatus status;

}
