package com.lean2708.auth_service.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.Date;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@RedisHash("RevokedToken")
public class RevokedToken {

    @Id
    private String accessToken;
    private String email;
    private Date expiryTime;

    @TimeToLive
    private long ttl;

}
