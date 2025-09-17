package com.lean2708.auth_service.dto.request;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserProfileRequest {

    private Long userId;
    private String phone;
    private String name;
    private String email;


}
