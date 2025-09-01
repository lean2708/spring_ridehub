package com.lean2708.auth_service.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("ForgotPasswordToken")
public class ForgotPasswordToken {

    @Id
    private String forgotPasswordToken;

    private String email;

    @TimeToLive
    private long ttl;
}
