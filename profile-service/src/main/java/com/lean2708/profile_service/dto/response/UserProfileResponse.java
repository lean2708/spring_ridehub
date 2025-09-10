package com.lean2708.profile_service.dto.response;

import lombok.*;
import java.time.LocalDate;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileResponse {

    private Long id;

    private Long userId;
    private String name;
    private String email;
    private String phone;

    private LocalDate dateOfBirth;
    private String avatarUrl;
    private LocalDate createdAt;

}
