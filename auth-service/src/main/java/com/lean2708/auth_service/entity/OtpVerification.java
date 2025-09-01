package com.lean2708.auth_service.entity;

import com.lean2708.auth_service.constants.OtpType;
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
@RedisHash(value = "user_otp")
public class OtpVerification {

    @Id
    private String redisKey;

    private String email;
    private String otp;

    @Enumerated(EnumType.STRING)
    private OtpType otpType;

    @TimeToLive
    long ttl;

}
