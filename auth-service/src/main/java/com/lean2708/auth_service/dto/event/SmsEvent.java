package com.lean2708.auth_service.dto.event;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsEvent {

    private String toPhone;
    private String otp;

}
