package com.lean2708.auth_service.entity;

import com.lean2708.auth_service.constants.RegistrationStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("user_registration")
public class UserRegistration {

    @Id
    private String email;
    private String name;
    private String phone;
    private String password;

    @Enumerated(EnumType.STRING)
    private RegistrationStatus status;

    @TimeToLive
    private long ttl;

}
